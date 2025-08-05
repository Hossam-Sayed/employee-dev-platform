package com.edp.library.service.learning;

import com.edp.library.data.entity.learning.Learning;
import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.entity.learning.LearningSubmissionTag;
import com.edp.library.data.entity.tag.Tag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.learning.LearningRepository;
import com.edp.library.data.repository.learning.LearningSubmissionRepository;
import com.edp.library.data.repository.learning.LearningSubmissionTagRepository;
import com.edp.library.data.repository.tag.TagRepository;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.library.mapper.LearningMapper;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;
import com.edp.library.model.learning.LearningTagDTO;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final LearningRepository learningRepository;
    private final LearningSubmissionRepository learningSubmissionRepository;
    private final LearningSubmissionTagRepository learningSubmissionTagRepository;
    private final TagRepository tagRepository;
    private final LearningMapper learningMapper;

    @Override
    @Transactional
    public LearningResponseDTO createLearning(LearningCreateRequestDTO request, Long submitterId, Long reviewerId) {
        // 1. Validate and fetch active tags
        List<Long> tagIds = request.getTags().stream().map(LearningTagDTO::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagRepository.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found. Provided IDs: " + tagIds + ", Found: " + tags.stream().map(Tag::getId).toList());
        }

        Map<Long, Tag> activeTagsMap = tags.stream()
                .filter(Tag::isActive)
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        if (activeTagsMap.size() != tagIds.size()) {
            Set<Long> inactiveTagIds = tagIds.stream()
                    .filter(id -> !activeTagsMap.containsKey(id))
                    .collect(Collectors.toSet());
            throw new InvalidOperationException("One or more selected tags are not active: " + inactiveTagIds);
        }

        // 3. Create a new Learning entity (as per your preferred logic, always create a new root Learning for a new request)
        Learning learning = Learning.builder()
                .employeeId(submitterId)
                .createdAt(Instant.now())
                .build();
        learning = learningRepository.save(learning);

        // 4. Create and save the new LearningSubmission for this new Learning
        LearningSubmission submission = learningMapper.toLearningSubmission(request, learning, submitterId, reviewerId);
        submission = learningSubmissionRepository.save(submission);

        // 5. Save LearningSubmissionTag entries
        Set<LearningSubmissionTag> submissionTags = new HashSet<>();
        for (LearningTagDTO tagDto : request.getTags()) {
            LearningSubmissionTag submissionTag = learningMapper.toLearningSubmissionTag(tagDto, submission);
            submissionTag.setTag(activeTagsMap.get(tagDto.getTagId()));
            submissionTags.add(submissionTag);
        }
        learningSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags);

        // 6. Set the current submission for the newly created Learning material
        learning.setCurrentSubmission(submission);
        learningRepository.save(learning);

        // TODO: Notification: As a USER, I need to receive notifications when my material submission status changes (e.g., pending on submission/approved/rejected).
        // Event data: employeeId (owner), submissionId, learningId, status=PENDING.
        // Triggered: On successful creation of a new submission.

        // TODO: Notification: As a MANAGER, I need to receive notifications when a new submission is assigned to me for review.
        // Event data: reviewerId, submissionId, learningId, submitterId.
        // Triggered: On successful creation of a new submission.

        return learningMapper.toLearningResponseDTO(learning);
    }

    @Override
    @Transactional
    public LearningResponseDTO editRejectedLearningSubmission(Long learningId, LearningCreateRequestDTO request, Long submitterId, Long reviewerId) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));

        // TODO: Revise this check if needed
        if (!learning.getEmployeeId().equals(submitterId)) {
            throw new InvalidOperationException("User is not authorized to edit this learning material.");
        }

        // Crucial check: Is the *current* submission for this learning material REJECTED?
        // And ensure there IS a current submission to begin with.
        if (learning.getCurrentSubmission() == null || learning.getCurrentSubmission().getStatus() != SubmissionStatus.REJECTED) {
            throw new InvalidOperationException("Only learning materials with a REJECTED current submission can be edited directly via this method. Current status: " +
                    (learning.getCurrentSubmission() != null ? learning.getCurrentSubmission().getStatus() : "N/A"));
        }

        // Validate tags
        List<Long> tagIds = request.getTags().stream().map(LearningTagDTO::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found. Provided IDs: " + tagIds + ", Found: " + tags.stream().map(Tag::getId).collect(Collectors.toList()));
        }
        Map<Long, Tag> activeTagsMap = tags.stream()
                .filter(Tag::isActive)
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        if (activeTagsMap.size() != tagIds.size()) {
            Set<Long> inactiveTagIds = tagIds.stream()
                    .filter(id -> !activeTagsMap.containsKey(id))
                    .collect(Collectors.toSet());
            throw new InvalidOperationException("One or more selected tags are not active: " + inactiveTagIds);
        }

        // Create and save the new LearningSubmission for the existing Learning material
        LearningSubmission newSubmission = learningMapper.toLearningSubmission(request, learning, submitterId, reviewerId);

        newSubmission = learningSubmissionRepository.save(newSubmission);

        // Save LearningSubmissionTag entries for the new submission
        Set<LearningSubmissionTag> newSubmissionTags = new HashSet<>();
        for (LearningTagDTO tagDto : request.getTags()) {
            LearningSubmissionTag submissionTag = learningMapper.toLearningSubmissionTag(tagDto, newSubmission);
            submissionTag.setTag(activeTagsMap.get(tagDto.getTagId()));
            newSubmissionTags.add(submissionTag);
        }
        learningSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Learning material's current submission to the newly created PENDING one
        learning.setCurrentSubmission(newSubmission);
        learningRepository.save(learning);

        // TODO: Notification: Similar to create, notify owner of new pending submission
        // TODO: Notification: Notify manager of new submission assigned for review

        return learningMapper.toLearningResponseDTO(learning);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningResponseDTO> getMyLearnings(Long employeeId, String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize()
        );

        Specification<Learning> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("employeeId"), employeeId));
            predicates.add(cb.isNotNull(root.get("currentSubmission")));

            if (StringUtils.hasText(statusFilter)) {
                try {
                    SubmissionStatus status = SubmissionStatus.valueOf(statusFilter.toUpperCase());
                    predicates.add(cb.equal(root.get("currentSubmission").get("status"), status));
                } catch (IllegalArgumentException e) {
                    throw new InvalidOperationException("Invalid status filter: " + statusFilter);
                }
            }

            if (tagIdFilter != null) {
                Objects.requireNonNull(query).distinct(true);
                predicates.add(cb.equal(root.join("currentSubmission").join("tags").get("tag").get("id"), tagIdFilter));
            }

            // Apply custom sort safely
            String sortBy = paginationRequestDTO.getSortBy();
            Sort.Direction direction = paginationRequestDTO.getSortDirection();
            Path<?> sortPath = switch (sortBy) {
                case "title", "submittedAt", "status" -> root.get("currentSubmission").get(sortBy);
                case "createdAt", "updatedAt" -> root.get(sortBy);
                default -> throw new InvalidOperationException("Unsupported sort field: " + sortBy);
            };

            Objects.requireNonNull(query).orderBy(
                    direction == Sort.Direction.DESC
                            ? cb.desc(sortPath)
                            : cb.asc(sortPath)
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };


        Page<Learning> learnings = learningRepository.findAll(spec, pageable);
        return mapToPaginationResponseDTO(learnings, learningMapper.toLearningResponseDTOs(learnings.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public LearningResponseDTO getLearningDetails(Long learningId) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));
        return learningMapper.toLearningResponseDTO(learning);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningSubmissionResponseDTO> getLearningSubmissionHistory(Long learningId, PaginationRequestDTO paginationRequestDTO) {
        // Ensure the learning exists, or the subsequent findByLearningId will return empty, which might be acceptable.
        learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));

        String actualSortBy = paginationRequestDTO.getSortBy();
        if ("createdAt".equalsIgnoreCase(actualSortBy)) {
            actualSortBy = "submittedAt"; // LearningSubmission uses submittedAt
        }

        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(paginationRequestDTO.getSortDirection(), actualSortBy)
        );
        Page<LearningSubmission> submissions = learningSubmissionRepository.findByLearningIdOrderBySubmittedAtDesc(learningId, pageable);
        return mapToPaginationResponseDTO(submissions, learningMapper.toLearningSubmissionResponseDTOs(submissions.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningSubmissionResponseDTO> getPendingLearningSubmissionsForReview(Long managerId, PaginationRequestDTO paginationRequestDTO) {

        String actualSortBy = paginationRequestDTO.getSortBy();
        if ("createdAt".equalsIgnoreCase(actualSortBy)) {
            actualSortBy = "submittedAt"; // LearningSubmission uses submittedAt
        }

        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(paginationRequestDTO.getSortDirection(), actualSortBy)
        );
        Page<LearningSubmission> pendingSubmissions = learningSubmissionRepository.findByReviewerIdAndStatus(managerId, SubmissionStatus.PENDING, pageable);
        return mapToPaginationResponseDTO(pendingSubmissions, learningMapper.toLearningSubmissionResponseDTOs(pendingSubmissions.getContent()));
    }

    @Override
    @Transactional
    public LearningSubmissionResponseDTO reviewLearningSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO, Long reviewerId) {
        LearningSubmission submission = learningSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        if (!submission.getReviewerId().equals(reviewerId)) {
            throw new InvalidOperationException("You are not authorized to review this submission.");
        }

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new InvalidOperationException("Submission is not in PENDING status and cannot be reviewed.");
        }

        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && !StringUtils.hasText(reviewDTO.getReviewerComment())) {
            throw new InvalidOperationException("Reviewer comment is required for rejecting a submission.");
        }
        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && reviewDTO.getReviewerComment().length() < 10) {
            throw new InvalidOperationException("Reviewer comment must be at least 10 characters long for rejection.");
        }

        submission.setStatus(learningMapper.toSubmissionStatusEntity(reviewDTO.getStatus()));
        // TODO: If the reviewed_at has an @UpdateTimestamp, will this line be removed?
        submission.setReviewedAt(Instant.now());
        submission.setReviewerComment(reviewDTO.getReviewerComment());

        LearningSubmission updatedSubmission = learningSubmissionRepository.save(submission);

        if (reviewDTO.getStatus() == SubmissionStatusDTO.APPROVED) {
            Learning learning = updatedSubmission.getLearning();
            // This ensures the 'currentSubmission' always points to the latest APPROVED one.
            // Only update if currentSubmission is not this approved one, or if its status is not APPROVED
            if (learning.getCurrentSubmission() == null || !learning.getCurrentSubmission().equals(updatedSubmission) || learning.getCurrentSubmission().getStatus() != SubmissionStatus.APPROVED) {
                learning.setCurrentSubmission(updatedSubmission);
//                learning.setUpdatedAt(Instant.now());
                learningRepository.save(learning);
            }
        }

        // TODO: Notification: As a MANAGER, I need the submitter to be notified when I approve or reject their submission.
        // Event data: submitterId, submissionId, learningId, newStatus, reviewerComment (if rejected)
        // Triggered: On approval/rejection of submission.

        // TODO: Notification: Notify the manager of their performed action (reviewing process)

        return learningMapper.toLearningSubmissionResponseDTO(updatedSubmission);
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