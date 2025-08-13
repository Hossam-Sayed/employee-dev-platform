package com.edp.library.service.wiki;

import com.edp.library.data.entity.wiki.Wiki;
import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.entity.wiki.WikiSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.wiki.WikiRepository;
import com.edp.library.data.repository.wiki.WikiSubmissionRepository;
import com.edp.library.data.repository.wiki.WikiSubmissionTagRepository;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.library.mapper.WikiMapper;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.wiki.WikiCreateRequestDTO;
import com.edp.library.model.wiki.WikiResponseDTO;
import com.edp.library.model.wiki.WikiSubmissionResponseDTO;
import com.edp.library.utils.PaginationUtils;
import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.shared.client.auth.model.UserProfileDto;
import com.edp.shared.client.file.FileServiceClient;
import com.edp.shared.client.file.model.FileResponseDto;
import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
import com.edp.shared.kafka.producer.KafkaProducer;
import com.edp.shared.security.jwt.JwtUserContext;
import com.edp.shared.kafka.model.NotificationDetails;
import com.edp.shared.kafka.model.SubmissionType;
import jakarta.persistence.criteria.Join;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WikiServiceImpl implements WikiService {

    private final WikiRepository wikiRepository;
    private final WikiSubmissionRepository wikiSubmissionRepository;
    private final WikiSubmissionTagRepository wikiSubmissionTagRepository;
    private final WikiMapper wikiMapper;
    private final AuthServiceClient authServiceClient;
    private final FileServiceClient fileServiceClient;
    private final TagServiceClient tagServiceClient;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public WikiResponseDTO createWiki(WikiCreateRequestDTO request, MultipartFile file) {
        // 1. Validate tags by calling the remote tag service
        List<Long> tagIds = request.getTagIds();
        String token = JwtUserContext.getToken();

        // Efficiently fetch all tags in a single remote call
        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty()) tags = tagServiceClient.findAllTagsByIds(tagIds, token);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(TagResponseDto::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
        }

        // 2. Validate file availability
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("No wiki file available. A wiki must have a file.");
        }

        // 3. Upload file to the remote file service
        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, token, false).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // 4. Create and save the new Wiki entity
        Long authorId = JwtUserContext.getUserId();
        Wiki wiki = wikiMapper.toWiki(authorId);
        wiki = wikiRepository.save(wiki);

        // 5. Create and save the new WikiSubmission
        final WikiSubmission submission = wikiSubmissionRepository.save(wikiMapper.toWikiSubmission(request, wiki, authorId, fileResponse.getId()));

        // 6. Create and save WikiSubmissionTag entities for the submission
        Set<WikiSubmissionTag> submissionTags = tagIds.stream()
                .map(tagId -> WikiSubmissionTag.builder()
                        .wikiSubmission(submission)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toSet());

        wikiSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags);

        // 7. Set the current submission for the newly created Wiki
        wiki.setCurrentSubmission(submission);
        wikiRepository.save(wiki);

        UserProfileDto userProfileDto = authServiceClient.getUserById(authorId, token);
        sendNotification(submission, userProfileDto.getReportsToId(), authorId);

        // Pass the fetched tags to the mapper
        return wikiMapper.toWikiResponseDTO(wiki, tags);
    }

    @Override
    @Transactional
    public WikiResponseDTO editRejectedWikiSubmission(Long wikiId, WikiCreateRequestDTO request, MultipartFile file) {
        Wiki wiki = wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki not found with ID: " + wikiId));

        Long authorId = JwtUserContext.getUserId();

        if (!wiki.getAuthorId().equals(authorId)) {
            throw new InvalidOperationException("User is not authorized to edit this wiki material.");
        }

        if (wiki.getCurrentSubmission() == null || wiki.getCurrentSubmission().getStatus() != SubmissionStatus.REJECTED) {
            throw new InvalidOperationException("Only wikis with a REJECTED current submission can be edited directly via this method. Current status: " +
                    (wiki.getCurrentSubmission() != null ? wiki.getCurrentSubmission().getStatus() : "N/A"));
        }

        // Validate tags by calling the remote tag service
        List<Long> tagIds = request.getTagIds();
        String token = JwtUserContext.getToken();

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty()) tags = tagServiceClient.findAllTagsByIds(tagIds, token);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(TagResponseDto::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
        }

        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("No wiki file available. A wiki must have a file.");
        }

        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, JwtUserContext.getToken(), true).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // Create and save the new WikiSubmission for the existing Wiki
        WikiSubmission newSubmission = wikiSubmissionRepository.save(wikiMapper.toWikiSubmission(request, wiki, authorId, fileResponse.getId()));

        // Save WikiSubmissionTag entries for the new submission
        Set<WikiSubmissionTag> newSubmissionTags = tagIds.stream()
                .map(tagId -> WikiSubmissionTag.builder()
                        .wikiSubmission(newSubmission)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toSet());

        wikiSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Wiki's current submission to the newly created PENDING one
        wiki.setCurrentSubmission(newSubmission);
        wikiRepository.save(wiki);

        UserProfileDto userProfileDto = authServiceClient.getUserById(authorId, token);
        sendNotification(newSubmission, userProfileDto.getReportsToId(), authorId);

        // Pass the fetched tags to the mapper
        return wikiMapper.toWikiResponseDTO(wiki, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiResponseDTO> getMyWikis(String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize()
        );

        Long authorId = JwtUserContext.getUserId();

        Specification<Wiki> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("authorId"), authorId));
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
                Join<Wiki, WikiSubmission> submissionJoin = root.join("currentSubmission");
                Join<WikiSubmission, WikiSubmissionTag> submissionTagJoin = submissionJoin.join("tags");
                predicates.add(cb.equal(submissionTagJoin.get("tagId"), tagIdFilter));
            }

            // Handle sorting manually
            String sortBy = paginationRequestDTO.getSortBy();
            Sort.Direction direction = paginationRequestDTO.getSortDirection();

            Path<?> sortPath = switch (sortBy) {
                case "title", "submittedAt", "status" -> root.get("currentSubmission").get(sortBy);
                case "createdAt", "updatedAt" -> root.get(sortBy);
                default -> throw new InvalidOperationException("Unsupported sort field: " + sortBy);
            };

            Objects.requireNonNull(query).orderBy(direction == Sort.Direction.ASC
                    ? cb.asc(sortPath)
                    : cb.desc(sortPath));

            return cb.and(predicates.toArray(new Predicate[0]));
        };


        Page<Wiki> wikis = wikiRepository.findAll(spec, pageable);

        Set<Long> tagIds = wikis.getContent().stream()
                .filter(wiki -> wiki.getCurrentSubmission() != null)
                .flatMap(wiki -> wiki.getCurrentSubmission().getTags().stream())
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return PaginationUtils.mapToPaginationResponseDTO(wikis, wikiMapper.toWikiResponseDTOs(wikis.getContent(), tags));
    }

    @Override
    @Transactional(readOnly = true)
    public WikiResponseDTO getWikiDetails(Long wikiId) {
        Wiki wiki = wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki not found with ID: " + wikiId));

        // TODO: AUTHORIZATION: Restrict access to PENDING/REJECTED wikis to the author or reviewer.
        // Publicly viewable wikis must be in APPROVED status.

        // Fetch tags for the single wiki
        if (wiki.getCurrentSubmission() == null) {
            throw new InvalidOperationException("Wiki does not have a current submission.");
        }

        Set<Long> tagIds = wiki.getCurrentSubmission().getTags().stream()
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return wikiMapper.toWikiResponseDTO(wiki, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiSubmissionResponseDTO> getWikiSubmissionHistory(Long wikiId, PaginationRequestDTO paginationRequestDTO) {
        // Ensure the wiki exists
        wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki not found with ID: " + wikiId));

        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, WikiSubmission.class);
        Page<WikiSubmission> submissions = wikiSubmissionRepository.findByWikiIdOrderBySubmittedAtDesc(wikiId, pageable);

        // Fetch tags for the submissions in the page
        Set<Long> tagIds = submissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return PaginationUtils.mapToPaginationResponseDTO(submissions, wikiMapper.toWikiSubmissionResponseDTOs(submissions.getContent(), tags));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiSubmissionResponseDTO> getPendingWikiSubmissionsForReview(PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, WikiSubmission.class);

        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();
        List<UserProfileDto> managedUsers = authServiceClient.getManagedUsers(managerId, token);
        List<Long> managedUserIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Page<WikiSubmission> pendingSubmissions = wikiSubmissionRepository.findBySubmitterIdInAndStatus(managedUserIds, SubmissionStatus.PENDING, pageable);

        // Fetch tags for the pending submissions in the page
        Set<Long> tagIds = pendingSubmissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return PaginationUtils.mapToPaginationResponseDTO(pendingSubmissions, wikiMapper.toWikiSubmissionResponseDTOs(pendingSubmissions.getContent(), tags));
    }

    @Override
    @Transactional
    public WikiSubmissionResponseDTO reviewWikiSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO) {
        WikiSubmission submission = wikiSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki Submission not found with ID: " + submissionId));

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
            throw new InvalidOperationException("Wiki Submission is not in PENDING status and cannot be reviewed.");
        }

        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && !StringUtils.hasText(reviewDTO.getReviewerComment())) {
            throw new InvalidOperationException("Reviewer comment is required for rejecting a submission.");
        }
        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && reviewDTO.getReviewerComment().length() < 10) {
            throw new InvalidOperationException("Reviewer comment must be at least 10 characters long for rejection.");
        }

        submission.setStatus(wikiMapper.toSubmissionStatusEntity(reviewDTO.getStatus()));
        submission.setReviewerId(reviewerId);
        submission.setReviewedAt(Instant.now());
        submission.setReviewerComment(reviewDTO.getReviewerComment());

        WikiSubmission updatedSubmission = wikiSubmissionRepository.save(submission);

        if (reviewDTO.getStatus() == SubmissionStatusDTO.APPROVED) {
            Wiki wiki = updatedSubmission.getWiki();
            // Update the 'currentSubmission' for the Wiki to point to this newly approved submission.
            if (wiki.getCurrentSubmission() == null || !wiki.getCurrentSubmission().equals(updatedSubmission) || wiki.getCurrentSubmission().getStatus() != SubmissionStatus.APPROVED) {
                wiki.setCurrentSubmission(updatedSubmission);
                wikiRepository.save(wiki);
            }
        }

        sendNotification(submission, submission.getSubmitterId(), reviewerId);

        // Fetch tags for the updated submission
        Set<Long> tagIds = updatedSubmission.getTags().stream()
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return wikiMapper.toWikiSubmissionResponseDTO(updatedSubmission, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiResponseDTO> getAllApprovedAndActiveWikis(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, Wiki.class);

        Specification<Wiki> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNotNull(root.get("currentSubmission")));
            predicates.add(cb.equal(root.get("currentSubmission").get("status"), SubmissionStatus.APPROVED));

            if (StringUtils.hasText(searchKeyword)) {
                String likePattern = "%" + searchKeyword.toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("currentSubmission").get("title")), likePattern);
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("currentSubmission").get("description")), likePattern);
                predicates.add(cb.or(titlePredicate, descriptionPredicate));
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                Objects.requireNonNull(query).distinct(true);
                Join<Wiki, WikiSubmission> submissionJoin = root.join("currentSubmission");
                Join<WikiSubmission, WikiSubmissionTag> submissionTagJoin = submissionJoin.join("tags");
                predicates.add(submissionTagJoin.get("tagId").in(tagIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Wiki> wikis = wikiRepository.findAll(spec, pageable);

        // Fetch tags for the entire page of wikis in one call
        Set<Long> uniqueTagIds = wikis.getContent().stream()
                .filter(wiki -> wiki.getCurrentSubmission() != null)
                .flatMap(wiki -> wiki.getCurrentSubmission().getTags().stream())
                .map(WikiSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(uniqueTagIds), JwtUserContext.getToken());

        // Pass the fetched tags to the mapper
        return PaginationUtils.mapToPaginationResponseDTO(wikis, wikiMapper.toWikiResponseDTOs(wikis.getContent(), tags));
    }

    private void sendNotification(WikiSubmission submission, Long ownerId, Long actorId) {
        NotificationDetails notificationDetails = NotificationDetails
                .builder()
                .title(submission.getTitle())
                .ownerId(ownerId)
                .actorId(actorId)
                .createdAt(Instant.now())
                .submissionType(SubmissionType.WIKI)
                .submissionId(submission.getId())
                .status(com.edp.shared.kafka.model.SubmissionStatus.PENDING).build();
        kafkaProducer.sendNotification(notificationDetails);
    }
}