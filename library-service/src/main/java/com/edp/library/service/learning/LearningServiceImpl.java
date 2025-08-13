package com.edp.library.service.learning;

import com.edp.library.data.entity.learning.Learning;
import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.entity.learning.LearningSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.learning.LearningRepository;
import com.edp.library.data.repository.learning.LearningSubmissionRepository;
import com.edp.library.data.repository.learning.LearningSubmissionTagRepository;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.library.mapper.LearningMapper;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;
import com.edp.library.model.learning.LearningTagDTO;
import com.edp.library.utils.PaginationUtils;
import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.shared.client.auth.model.UserProfileDto;
import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
import com.edp.shared.kafka.model.LearningProgress;
import com.edp.shared.security.jwt.JwtUserContext;
import com.edp.shared.kafka.model.NotificationDetails;
import com.edp.shared.kafka.model.SubmissionType;
import com.edp.shared.kafka.producer.KafkaProducer;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final LearningRepository learningRepository;
    private final LearningSubmissionRepository learningSubmissionRepository;
    private final LearningSubmissionTagRepository learningSubmissionTagRepository;
    private final LearningMapper learningMapper;
    private final AuthServiceClient authServiceClient;
    private final TagServiceClient tagServiceClient;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public LearningResponseDTO createLearning(LearningCreateRequestDTO request) {
        // 1. Validate tags by calling the remote tag service
        List<Long> tagIds = request.getTags().stream().map(LearningTagDTO::getTagId).collect(Collectors.toList());
        String token = JwtUserContext.getToken();

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty()) tags = tagServiceClient.findAllTagsByIds(tagIds, token);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(TagResponseDto::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
        }

        Long submitterId = JwtUserContext.getUserId();

        // 2. Create a new Learning entity
        Learning learning = Learning.builder()
                .employeeId(submitterId)
                .build();
        learning = learningRepository.save(learning);

        // 3. Create and save the new LearningSubmission for this new Learning
        LearningSubmission submission = learningSubmissionRepository.save(learningMapper.toLearningSubmission(request, learning, submitterId));

        // 4. Save LearningSubmissionTag entries
        Set<LearningSubmissionTag> submissionTags = request.getTags().stream()
                .map(tagDto -> learningMapper.toLearningSubmissionTag(tagDto, submission))
                .collect(Collectors.toSet());

        learningSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags);

        // 5. Set the current submission for the newly created Learning material
        learning.setCurrentSubmission(submission);
        learningRepository.save(learning);

        UserProfileDto userProfileDto = authServiceClient.getUserById(submitterId, token);
        sendNotification(submission, userProfileDto.getReportsToId(), submitterId);

        return learningMapper.toLearningResponseDTO(learning, tags);
    }

    @Override
    @Transactional
    public LearningResponseDTO editRejectedLearningSubmission(Long learningId, LearningCreateRequestDTO request) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));

        Long submitterId = JwtUserContext.getUserId();

        if (!learning.getEmployeeId().equals(submitterId)) {
            throw new InvalidOperationException("User is not authorized to edit this learning material.");
        }

        if (learning.getCurrentSubmission() == null || learning.getCurrentSubmission().getStatus() != SubmissionStatus.REJECTED) {
            throw new InvalidOperationException("Only learning materials with a REJECTED current submission can be edited directly via this method. Current status: " +
                    (learning.getCurrentSubmission() != null ? learning.getCurrentSubmission().getStatus() : "N/A"));
        }

        // Validate tags
        List<Long> tagIds = request.getTags().stream().map(LearningTagDTO::getTagId).collect(Collectors.toList());
        String token = JwtUserContext.getToken();

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty()) tags = tagServiceClient.findAllTagsByIds(tagIds, token);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(TagResponseDto::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
        }

        // Create and save the new LearningSubmission for the existing Learning material
        LearningSubmission newSubmission = learningSubmissionRepository.save(learningMapper.toLearningSubmission(request, learning, submitterId));

        // Save LearningSubmissionTag entries for the new submission
        Set<LearningSubmissionTag> newSubmissionTags = request.getTags().stream()
                .map(tagDto -> learningMapper.toLearningSubmissionTag(tagDto, newSubmission))
                .collect(Collectors.toSet());

        learningSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Learning material's current submission to the newly created PENDING one
        learning.setCurrentSubmission(newSubmission);
        learningRepository.save(learning);

        UserProfileDto userProfileDto = authServiceClient.getUserById(submitterId, token);
        sendNotification(newSubmission, userProfileDto.getReportsToId(), submitterId);

        return learningMapper.toLearningResponseDTO(learning, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningResponseDTO> getMyLearnings(String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize()
        );

        Long employeeId = JwtUserContext.getUserId();

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
                predicates.add(cb.equal(root.join("currentSubmission").join("tags").get("tagId"), tagIdFilter));
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

        Set<Long> tagIds = learnings.getContent().stream()
                .filter(learning -> learning.getCurrentSubmission() != null)
                .flatMap(learning -> learning.getCurrentSubmission().getTags().stream())
                .map(LearningSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(learnings, learningMapper.toLearningResponseDTOs(learnings.getContent(), tags));
    }

    @Override
    @Transactional(readOnly = true)
    public LearningResponseDTO getLearningDetails(Long learningId) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));

        if (learning.getCurrentSubmission() == null) {
            throw new InvalidOperationException("Learning does not have a current submission.");
        }

        Set<Long> tagIds = learning.getCurrentSubmission().getTags().stream()
                .map(LearningSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return learningMapper.toLearningResponseDTO(learning, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningSubmissionResponseDTO> getLearningSubmissionHistory(Long learningId, PaginationRequestDTO paginationRequestDTO) {
        learningRepository.findById(learningId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning material not found with ID: " + learningId));

        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, LearningSubmission.class);
        Page<LearningSubmission> submissions = learningSubmissionRepository.findByLearningIdOrderBySubmittedAtDesc(learningId, pageable);

        Set<Long> tagIds = submissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(LearningSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(submissions, learningMapper.toLearningSubmissionResponseDTOs(submissions.getContent(), tags));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<LearningSubmissionResponseDTO> getPendingLearningSubmissionsForReview(PaginationRequestDTO paginationRequestDTO) {
        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();
        List<UserProfileDto> managedUsers = authServiceClient.getManagedUsers(managerId, token);
        List<Long> managedUserIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, LearningSubmission.class);
        Page<LearningSubmission> pendingSubmissions = learningSubmissionRepository.findBySubmitterIdInAndStatus(managedUserIds, SubmissionStatus.PENDING, pageable);

        Set<Long> tagIds = pendingSubmissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(LearningSubmissionTag::getTagId)
                .collect(Collectors.toSet());
        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(pendingSubmissions, learningMapper.toLearningSubmissionResponseDTOs(pendingSubmissions.getContent(), tags));
    }

    @Override
    @Transactional
    public LearningSubmissionResponseDTO reviewLearningSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO) {
        LearningSubmission submission = learningSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        Long reviewerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();
        List<UserProfileDto> managedUsers = authServiceClient.getManagedUsers(reviewerId, token);

        Set<Long> managedUserIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .collect(Collectors.toSet());

        boolean isResponsible = managedUserIds.contains(submission.getSubmitterId());

        if (!isResponsible) {
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
        submission.setReviewerId(reviewerId);
        submission.setReviewedAt(Instant.now());
        submission.setReviewerComment(reviewDTO.getReviewerComment());

        LearningSubmission updatedSubmission = learningSubmissionRepository.save(submission);

        if (reviewDTO.getStatus() == SubmissionStatusDTO.APPROVED) {
            Learning learning = updatedSubmission.getLearning();
            if (learning.getCurrentSubmission() == null || !learning.getCurrentSubmission().equals(updatedSubmission) || learning.getCurrentSubmission().getStatus() != SubmissionStatus.APPROVED) {
                learning.setCurrentSubmission(updatedSubmission);
                learningRepository.save(learning);
            }
            // TODO: Add Kafka producer call HERE
            LearningProgress learningProgress = LearningProgress.builder()
                    .userID(submission.getSubmitterId()).proofUrl(submission.getProofUrl()).updates(submission.getTags().stream()
                            .collect(Collectors.toMap(
                                    LearningSubmissionTag::getId,
                                    tag -> (double) tag.getDurationMinutes()
                            )))
                    .build();

            kafkaProducer.sendLearningProgress(learningProgress);
            //I ONLY ADDED THE FEW LINES ABOVE TO SEND THE TOPIC
        }

        sendNotification(submission, submission.getSubmitterId(), reviewerId);

        Set<Long> tagIds = updatedSubmission.getTags().stream()
                .map(LearningSubmissionTag::getTagId)
                .collect(Collectors.toSet());
        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return learningMapper.toLearningSubmissionResponseDTO(updatedSubmission, tags);
    }

    private void sendNotification(LearningSubmission submission, Long ownerId, Long actorId) {
        NotificationDetails notificationDetails = NotificationDetails
                .builder()
                .title(submission.getTitle())
                .ownerId(ownerId)
                .actorId(actorId)
                .createdAt(Instant.now())
                .submissionType(SubmissionType.LEARNING)
                .submissionId(submission.getId())
                .status(com.edp.shared.kafka.model.SubmissionStatus.PENDING).build();
        kafkaProducer.sendNotification(notificationDetails);
    }
}