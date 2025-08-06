package com.edp.library.service.tag;

import com.edp.library.data.entity.tag.Tag;
import com.edp.library.data.entity.tag.TagRequest;
import com.edp.library.data.enums.TagRequestStatus;
import com.edp.library.data.repository.tag.TagRepository;
import com.edp.library.data.repository.tag.TagRequestRepository;
import com.edp.library.exception.ResourceAlreadyExistsException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.mapper.TagMapper;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.enums.TagRequestStatusDTO;
import com.edp.library.model.tag.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagRequestRepository tagRequestRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public TagRequestResponseDTO createTagRequest(TagCreateRequestDTO request, Long requesterId) {
        if (isDuplicateTagRequestOrApprovedTag(request.getRequestedName())) {
            throw new ResourceAlreadyExistsException("A tag with name '" + request.getRequestedName() + "' is already approved, pending, or rejected.");
        }

        TagRequest tagRequest = tagMapper.toTagRequest(request, requesterId);
        tagRequest.setCreatedAt(Instant.now());
        tagRequest.setStatus(TagRequestStatus.PENDING); // Ensure status is PENDING

        tagRequest = tagRequestRepository.save(tagRequest);

        // TODO: Notification: As a USER, I need to be notified if my tag request is approved or rejected.
        // Initial notification: "Your tag request for 'X' has been submitted and is pending review."
        // Event data: requesterId, tagRequestId, requestedName, status.
        // Triggered: On successful creation of tag request.

        // TODO: Notification: As an ADMIN, I need to receive notifications when a new tag request is submitted.
        // Event data: tagRequestId, requestedName, requesterId.
        // Triggered: On successful creation of tag request.

        return tagMapper.toTagRequestResponseDTO(tagRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateTagRequestOrApprovedTag(String tagName) {
        // Check for existing approved tags (case-insensitive)
        Optional<Tag> existingApprovedTag = tagRepository.findByNameIgnoreCase(tagName);
        if (existingApprovedTag.isPresent()) {
            return true;
        }

        // Check for existing tag requests (case-insensitive)
        Optional<TagRequest> existingPendingRequest = tagRequestRepository.findByRequestedNameIgnoreCase(tagName);
        return existingPendingRequest.isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> getAllApprovedAndActiveTags(String nameFilter) {
        List<Tag> tags = StringUtils.hasText(nameFilter) ?
                tagRepository.findByNameContainingIgnoreCaseAndActive(nameFilter, true) :
                tagRepository.findByActive(true);
        return tagMapper.toTagDTOs(tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<TagRequestResponseDTO> getMyTagRequests(Long requesterId, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(Objects.requireNonNullElse(paginationRequestDTO.getSortDirection(), Sort.Direction.DESC), paginationRequestDTO.getSortBy())
        );
        Page<TagRequest> tagRequests = tagRequestRepository.findByRequesterId(requesterId, pageable);
        return mapToPaginationResponseDTO(tagRequests, tagMapper.toTagRequestResponseDTOs(tagRequests.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<TagRequestResponseDTO> getAllPendingTagRequests(PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(Objects.requireNonNullElse(paginationRequestDTO.getSortDirection(), Sort.Direction.DESC), paginationRequestDTO.getSortBy())
        );
        Page<TagRequest> pendingRequests = tagRequestRepository.findByStatus(TagRequestStatus.PENDING, pageable);
        return mapToPaginationResponseDTO(pendingRequests, tagMapper.toTagRequestResponseDTOs(pendingRequests.getContent()));
    }

    @Override
    @Transactional
    public TagRequestResponseDTO reviewTagRequest(Long tagRequestId, TagRequestReviewDTO reviewDTO, Long reviewerId) {
        TagRequest tagRequest = tagRequestRepository.findById(tagRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag Request not found with ID: " + tagRequestId));

        if (tagRequest.getStatus() != TagRequestStatus.PENDING) {
            throw new InvalidOperationException("Tag request is not in PENDING status and cannot be reviewed.");
        }

        // Conditional validation for reviewerComment
        if (reviewDTO.getStatus() == TagRequestStatusDTO.REJECTED && !StringUtils.hasText(reviewDTO.getReviewerComment())) {
            throw new InvalidOperationException("Reviewer comment is required for rejecting a tag request.");
        }
        if (reviewDTO.getStatus() == TagRequestStatusDTO.REJECTED && reviewDTO.getReviewerComment().length() < 10) {
            throw new InvalidOperationException("Reviewer comment must be at least 10 characters long for rejection.");
        }

        tagRequest.setStatus(tagMapper.toTagRequestStatusEntity(reviewDTO.getStatus()));
        tagRequest.setReviewerId(reviewerId);
        tagRequest.setReviewedAt(Instant.now());
        tagRequest.setReviewerComment(reviewDTO.getReviewerComment());

        if (reviewDTO.getStatus() == TagRequestStatusDTO.APPROVED) {
            // User Story: As an ADMIN, when I approve a tag request, the system should automatically create a new entry in the main 'Tag' table.
            if (tagRepository.findByNameIgnoreCase(tagRequest.getRequestedName()).isPresent()) {
                throw new ResourceAlreadyExistsException("An active tag with the name '" + tagRequest.getRequestedName() + "' already exists. Cannot approve duplicate.");
            }
            Tag newTag = Tag.builder()
                    .name(tagRequest.getRequestedName())
                    .createdBy(reviewerId) // Admin who approved it
                    .createdAt(Instant.now())
                    .active(true)
                    .build();
            tagRepository.save(newTag);
        }

        tagRequest = tagRequestRepository.save(tagRequest);

        // TODO: Notification: As a USER, I need to be notified if my tag request is approved or rejected.
        // Event data: requesterId, tagRequestId, requestedName, newStatus, reviewerComment (if rejected)
        // Triggered: On approval/rejection of tag request.

        return tagMapper.toTagRequestResponseDTO(tagRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<TagDTO> getAllTagsForAdmin(String nameFilter, Boolean isActiveFilter, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(Objects.requireNonNullElse(paginationRequestDTO.getSortDirection(), Sort.Direction.DESC), paginationRequestDTO.getSortBy())
        );

        Page<Tag> tags;
        if (StringUtils.hasText(nameFilter) && isActiveFilter != null) {
            tags = tagRepository.findByNameContainingIgnoreCaseAndActive(nameFilter, isActiveFilter, pageable);
        } else if (StringUtils.hasText(nameFilter)) {
            tags = tagRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else if (isActiveFilter != null) {
            tags = tagRepository.findByActive(isActiveFilter, pageable);
        } else {
            tags = tagRepository.findAll(pageable);
        }

        return mapToPaginationResponseDTO(tags, tagMapper.toTagDTOs(tags.getContent()));
    }

    @Override
    @Transactional
    public TagDTO updateTagStatus(Long tagId, TagUpdateStatusDTO updateStatusDTO, Long adminId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));

        if (tag.isActive() == updateStatusDTO.getActive()) {
            throw new InvalidOperationException("Tag is already in the requested active status.");
        }

        tag.setActive(updateStatusDTO.getActive());
        // Optionally, record who updated it and when if such fields exist in Tag entity
        // tag.setUpdatedBy(adminId);
        // tag.setUpdatedAt(Instant.now());

        tagRepository.save(tag);
        return tagMapper.toTagDTO(tag);
    }

    @Override
    @Transactional
    public TagDTO createTagByAdmin(TagCreateRequestDTO tagCreateRequestDTO, Long adminId) {
        // 1. Validate input
        if (!StringUtils.hasText(tagCreateRequestDTO.getRequestedName())) {
            throw new IllegalArgumentException("Tag name cannot be null or empty.");
        }

        // 2. Check for duplicate tag name (case-insensitive)
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tagCreateRequestDTO.getRequestedName());
        if (existingTag.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    String.format("Tag with name '%s' already exists.", tagCreateRequestDTO.getRequestedName())
            );
        }

        // 3. Create new Tag entity
        Tag newTag = Tag.builder()
                .name(tagCreateRequestDTO.getRequestedName())
                .createdBy(adminId)
                .createdAt(Instant.now())
                .active(true)
                .build();

        // 4. Save the new tag
        Tag savedTag = tagRepository.save(newTag);

        // 5. Return the response DTO
        return tagMapper.toTagDTO(savedTag);
    }

    // Helper method for pagination response mapping
    private <T, U> PaginationResponseDTO<U> mapToPaginationResponseDTO(Page<T> page, List<U> content) {
        return PaginationResponseDTO.<U>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}