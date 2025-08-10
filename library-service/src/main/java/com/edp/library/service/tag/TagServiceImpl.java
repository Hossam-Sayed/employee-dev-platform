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
import com.edp.library.utils.PaginationUtils;
import com.edp.shared.security.jwt.JwtUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
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
    public TagRequestResponseDTO createTagRequest(TagCreateRequestDTO request) {
        if (isDuplicateTagRequestOrApprovedTag(request.getRequestedName())) {
            throw new ResourceAlreadyExistsException("A tag with name '" + request.getRequestedName() + "' is already approved, pending, or rejected.");
        }
        Long requesterId = JwtUserContext.getUserId();
        TagRequest tagRequest = tagMapper.toTagRequest(request, requesterId);
        tagRequest.setCreatedAt(Instant.now());
        tagRequest.setStatus(TagRequestStatus.PENDING); // Ensure status is PENDING

        tagRequest = tagRequestRepository.save(tagRequest);

        // TODO: Notification: As a USER, I need to be notified if my tag request is approved or rejected.
        //  Initial notification: "Your tag request for 'X' has been submitted and is pending review."
        //  Event data: requesterId, tagRequestId, requestedName, status.
        //  Triggered: On successful creation of tag request.

        // TODO: Notification: As an ADMIN, I need to receive notifications when a new tag request is submitted.
        //  Event data: tagRequestId, requestedName, requesterId.
        //  Triggered: On successful creation of tag request.

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
    public PaginationResponseDTO<TagRequestResponseDTO> getMyTagRequests(PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, Tag.class);
        Long requesterId = JwtUserContext.getUserId();
        Page<TagRequest> tagRequests = tagRequestRepository.findByRequesterId(requesterId, pageable);
        return PaginationUtils.mapToPaginationResponseDTO(tagRequests, tagMapper.toTagRequestResponseDTOs(tagRequests.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<TagRequestResponseDTO> getAllPendingTagRequests(PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, Tag.class);
        Page<TagRequest> pendingRequests = tagRequestRepository.findByStatus(TagRequestStatus.PENDING, pageable);
        return PaginationUtils.mapToPaginationResponseDTO(pendingRequests, tagMapper.toTagRequestResponseDTOs(pendingRequests.getContent()));
    }

    @Override
    @Transactional
    public TagRequestResponseDTO reviewTagRequest(Long tagRequestId, TagRequestReviewDTO reviewDTO) {
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

        Long adminId = JwtUserContext.getUserId();

        tagRequest.setStatus(tagMapper.toTagRequestStatusEntity(reviewDTO.getStatus()));
        tagRequest.setReviewerId(adminId);
        tagRequest.setReviewedAt(Instant.now());
        tagRequest.setReviewerComment(reviewDTO.getReviewerComment());

        if (reviewDTO.getStatus() == TagRequestStatusDTO.APPROVED) {
            // User Story: As an ADMIN, when I approve a tag request, the system should automatically create a new entry in the main 'Tag' table.
            if (tagRepository.findByNameIgnoreCase(tagRequest.getRequestedName()).isPresent()) {
                throw new ResourceAlreadyExistsException("An active tag with the name '" + tagRequest.getRequestedName() + "' already exists. Cannot approve duplicate.");
            }
            Tag newTag = Tag.builder()
                    .name(tagRequest.getRequestedName())
                    .createdBy(adminId) // Admin who approved it
                    .createdAt(Instant.now())
                    .active(true)
                    .build();
            tagRepository.save(newTag);
        }

        tagRequest = tagRequestRepository.save(tagRequest);

        // TODO: Notification: As a USER, I need to be notified if my tag request is approved or rejected.
        //  Event data: requesterId, tagRequestId, requestedName, newStatus, reviewerComment (if rejected)
        //  Triggered: On approval/rejection of tag request.

        return tagMapper.toTagRequestResponseDTO(tagRequest);
    }
}