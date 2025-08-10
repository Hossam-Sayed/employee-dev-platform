package com.edp.library.service.wiki;

import com.edp.library.data.entity.tag.Tag;
import com.edp.library.data.entity.wiki.Wiki;
import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.entity.wiki.WikiSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.tag.TagRepository;
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
import com.edp.shared.security.jwt.JwtUserContext;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WikiServiceImpl implements WikiService {

    private final WikiRepository wikiRepository;
    private final WikiSubmissionRepository wikiSubmissionRepository;
    private final WikiSubmissionTagRepository wikiSubmissionTagRepository;
    private final TagRepository tagRepository;
    private final WikiMapper wikiMapper;
    private final AuthServiceClient authServiceClient;
    private final FileServiceClient fileServiceClient;

    @Override
    @Transactional
    public WikiResponseDTO createWiki(WikiCreateRequestDTO request, MultipartFile file) {
        // TODO: AUTHENTICATION: Ensure the authorId corresponds to a valid authenticated user.
        // TODO: AUTHORIZATION: Verify the authenticated user has permission to create a wiki.

        // 1. Validate and fetch active tags
        List<Long> tagIds = request.getTagIds();
        List<Tag> tags = tagRepository.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
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

        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("No blog file available. A blog must have a file.");
        }

        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, JwtUserContext.getToken(), true).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // 2. Create a new Wiki entity
        Long authorId = JwtUserContext.getUserId();
        Wiki wiki = wikiMapper.toWiki(authorId);
        wiki = wikiRepository.save(wiki);

        // 3. Create and save the new WikiSubmission for this new Wiki
        WikiSubmission submission = wikiMapper.toWikiSubmission(request, wiki, authorId, fileResponse.getId());
        submission = wikiSubmissionRepository.save(submission);

        // 4. Save WikiSubmissionTag entries
        Set<WikiSubmissionTag> submissionTags = new HashSet<>();
        for (Long tagId : request.getTagIds()) {
            Tag tag = activeTagsMap.get(tagId);
            WikiSubmissionTag wikiSubmissionTag = WikiSubmissionTag.builder()
                    .wikiSubmission(submission)
                    .tag(tag)
                    .build();
            submissionTags.add(wikiSubmissionTag);
        }
        wikiSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags);

        // 5. Set the current submission for the newly created Wiki
        wiki.setCurrentSubmission(submission);
        wikiRepository.save(wiki);

        // TODO: Notification: As a USER, I need to receive notifications when my wiki submission status changes.
        // TODO: Notification: As a MANAGER, I need to receive notifications when a new wiki submission is assigned to me for review.

        return wikiMapper.toWikiResponseDTO(wiki);
    }

    @Override
    @Transactional
    public WikiResponseDTO editRejectedWikiSubmission(Long wikiId, WikiCreateRequestDTO request, MultipartFile file) {
        // TODO: AUTHENTICATION: Ensure the authorId corresponds to a valid authenticated user.
        // TODO: AUTHORIZATION: Verify the authenticated user has permission to edit this wiki (i.e., is the author).
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

        // Validate tags
        List<Long> tagIds = request.getTagIds();
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
            Set<Long> missingTagIds = new HashSet<>(tagIds);
            missingTagIds.removeAll(foundTagIds);
            throw new ResourceNotFoundException("One or more tags not found. Missing IDs: " + missingTagIds);
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

        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("No blog file available. A blog must have a file.");
        }

        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, JwtUserContext.getToken(), true).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // Create and save the new WikiSubmission for the existing Wiki
        WikiSubmission newSubmission = wikiMapper.toWikiSubmission(request, wiki, authorId, fileResponse.getId());
        newSubmission = wikiSubmissionRepository.save(newSubmission);

        // Save WikiSubmissionTag entries for the new submission
        Set<WikiSubmissionTag> newSubmissionTags = new HashSet<>();
        for (Long tagId : request.getTagIds()) {
            Tag tag = activeTagsMap.get(tagId);
            WikiSubmissionTag submissionTag = WikiSubmissionTag.builder()
                    .wikiSubmission(newSubmission)
                    .tag(tag)
                    .build();
            newSubmissionTags.add(submissionTag);
        }
        wikiSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Wiki's current submission to the newly created PENDING one
        wiki.setCurrentSubmission(newSubmission);
        wikiRepository.save(wiki);

        // TODO: Notification: Similar to create, notify owner of new pending submission
        // TODO: Notification: Notify manager of new submission assigned for review

        return wikiMapper.toWikiResponseDTO(wiki);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiResponseDTO> getMyWikis(String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO) {
        // TODO: AUTHORIZATION: Verify the authenticated user's ID matches the authorId in the request header.
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
                predicates.add(cb.equal(submissionTagJoin.get("tag").get("id"), tagIdFilter));
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
        return PaginationUtils.mapToPaginationResponseDTO(wikis, wikiMapper.toWikiResponseDTOs(wikis.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public WikiResponseDTO getWikiDetails(Long wikiId) {
        Wiki wiki = wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki not found with ID: " + wikiId));

        // TODO: AUTHORIZATION: Restrict access to PENDING/REJECTED wikis to the author or reviewer.
        // Publicly viewable wikis must be in APPROVED status.

        return wikiMapper.toWikiResponseDTO(wiki);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiSubmissionResponseDTO> getWikiSubmissionHistory(Long wikiId, PaginationRequestDTO paginationRequestDTO) {
        // Ensure the wiki exists
        wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki not found with ID: " + wikiId));

        // TODO: AUTHORIZATION: Restrict this endpoint. Only the author or a reviewer should be able to see the full submission history.
        // A regular user might only be able to see the current APPROVED submission.

        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, WikiSubmission.class);
        Page<WikiSubmission> submissions = wikiSubmissionRepository.findByWikiIdOrderBySubmittedAtDesc(wikiId, pageable);
        return PaginationUtils.mapToPaginationResponseDTO(submissions, wikiMapper.toWikiSubmissionResponseDTOs(submissions.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiSubmissionResponseDTO> getPendingWikiSubmissionsForReview(PaginationRequestDTO paginationRequestDTO) {
        // TODO: AUTHORIZATION: Verify the authenticated user's ID matches the reviewerId in the request header.
        // TODO: AUTHORIZATION: Verify the authenticated user has a 'REVIEWER' or 'MANAGER' role.
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, WikiSubmission.class);

        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();
        List<UserProfileDto> managedUsers = authServiceClient.getManagedUsers(managerId, token);
        List<Long> managedUserIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Page<WikiSubmission> pendingSubmissions = wikiSubmissionRepository.findBySubmitterIdInAndStatus(managedUserIds, SubmissionStatus.PENDING, pageable);
        return PaginationUtils.mapToPaginationResponseDTO(pendingSubmissions, wikiMapper.toWikiSubmissionResponseDTOs(pendingSubmissions.getContent()));
    }

    @Override
    @Transactional
    public WikiSubmissionResponseDTO reviewWikiSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO) {
        // TODO: AUTHORIZATION: Verify the authenticated user's ID matches the reviewerId in the request header.
        // TODO: AUTHORIZATION: Verify the authenticated user has a 'REVIEWER' or 'MANAGER' role.
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

        // TODO: Notification: Notify submitter about status change.
        // TODO: Notification: Notify reviewer of their performed action.

        return wikiMapper.toWikiSubmissionResponseDTO(updatedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<WikiResponseDTO> getAllApprovedAndActiveWikis(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO) {
        // TODO: AUTHENTICATION: This endpoint may not require authentication, but its logic might change if user roles are introduced (e.g., an 'admin' can see more).
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
                predicates.add(submissionTagJoin.get("tag").get("id").in(tagIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Wiki> wikis = wikiRepository.findAll(spec, pageable);
        return PaginationUtils.mapToPaginationResponseDTO(wikis, wikiMapper.toWikiResponseDTOs(wikis.getContent()));
    }
}