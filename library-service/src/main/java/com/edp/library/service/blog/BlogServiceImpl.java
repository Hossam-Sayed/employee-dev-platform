package com.edp.library.service.blog;

import com.edp.library.data.entity.blog.Blog;
import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.entity.blog.BlogSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.blog.BlogRepository;
import com.edp.library.data.repository.blog.BlogSubmissionRepository;
import com.edp.library.data.repository.blog.BlogSubmissionTagRepository;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.shared.kafka.model.NotificationDetails;
import com.edp.shared.kafka.model.SubmissionType;
import com.edp.shared.kafka.producer.KafkaProducer;
import com.edp.library.mapper.BlogMapper;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.utils.PaginationUtils;
import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.shared.client.auth.model.UserProfileDto;
import com.edp.shared.client.file.FileServiceClient;
import com.edp.shared.client.file.model.FileResponseDto;
import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogSubmissionRepository blogSubmissionRepository;
    private final BlogSubmissionTagRepository blogSubmissionTagRepository;
    private final BlogMapper blogMapper;
    private final AuthServiceClient authServiceClient;
    private final FileServiceClient fileServiceClient;
    private final TagServiceClient tagServiceClient;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public BlogResponseDTO createBlog(BlogCreateRequestDTO request, MultipartFile file) {
        // 1. Validate tags by calling the remote tag service
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

        // 2. Validate file availability
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("No blog file available. A blog must have a file.");
        }

        // 3. Upload file to the remote file service
        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, token, false).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // 4. Create and save the new Blog entity
        Long authorId = JwtUserContext.getUserId();
        Blog blog = blogMapper.toBlog(authorId);
        blog = blogRepository.save(blog);

        // 5. Create and save the new BlogSubmission
        final BlogSubmission submission = blogSubmissionRepository.save(blogMapper.toBlogSubmission(request, blog, authorId, fileResponse.getId()));

        // 6. Create and save BlogSubmissionTag entities for the submission
        Set<BlogSubmissionTag> submissionTags = tagIds.stream()
                .map(tagId -> BlogSubmissionTag.builder()
                        .blogSubmission(submission)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toSet());

        blogSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags);

        // 7. Set the current submission for the newly created Blog
        blog.setCurrentSubmission(submission);
        blogRepository.save(blog);

        UserProfileDto userProfileDto = authServiceClient.getUserById(authorId, token);
        sendNotification(submission, userProfileDto.getReportsToId(), authorId);

        // Pass the fetched tags to the mapper
        return blogMapper.toBlogResponseDTO(blog, tags);
    }

    @Override
    @Transactional
    public BlogResponseDTO editRejectedBlogSubmission(Long blogId, BlogCreateRequestDTO request, MultipartFile file) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        Long authorId = JwtUserContext.getUserId();

        if (!blog.getAuthorId().equals(authorId)) {
            throw new InvalidOperationException("User is not authorized to edit this blog material.");
        }

        if (blog.getCurrentSubmission() == null || blog.getCurrentSubmission().getStatus() != SubmissionStatus.REJECTED) {
            throw new InvalidOperationException("Only blogs with a REJECTED current submission can be edited directly via this method. Current status: " +
                    (blog.getCurrentSubmission() != null ? blog.getCurrentSubmission().getStatus() : "N/A"));
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
            throw new InvalidOperationException("No blog file available. A blog must have a file.");
        }

        FileResponseDto fileResponse = fileServiceClient.uploadFile(file, JwtUserContext.getToken(), true).getBody();

        if (fileResponse == null) {
            throw new InvalidOperationException("Something went wrong while uploading the file! Please try again!");
        }

        // Create and save the new BlogSubmission for the existing Blog
        BlogSubmission newSubmission = blogSubmissionRepository.save(blogMapper.toBlogSubmission(request, blog, authorId, fileResponse.getId()));

        // Save BlogSubmissionTag entries for the new submission
        Set<BlogSubmissionTag> newSubmissionTags = tagIds.stream()
                .map(tagId -> BlogSubmissionTag.builder()
                        .blogSubmission(newSubmission)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toSet());

        blogSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Blog's current submission to the newly created PENDING one
        blog.setCurrentSubmission(newSubmission);
        blogRepository.save(blog);

        UserProfileDto userProfileDto = authServiceClient.getUserById(authorId, token);
        sendNotification(newSubmission, userProfileDto.getReportsToId(), authorId);

        // Pass the fetched tags to the mapper
        return blogMapper.toBlogResponseDTO(blog, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogResponseDTO> getMyBlogs(String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize()
        );

        Long authorId = JwtUserContext.getUserId();

        Specification<Blog> spec = (root, query, cb) -> {
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
                Join<Blog, BlogSubmission> submissionJoin = root.join("currentSubmission");
                Join<BlogSubmission, BlogSubmissionTag> submissionTagJoin = submissionJoin.join("tags");
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


        Page<Blog> blogs = blogRepository.findAll(spec, pageable);

        Set<Long> tagIds = blogs.getContent().stream()
                .filter(blog -> blog.getCurrentSubmission() != null)
                .flatMap(blog -> blog.getCurrentSubmission().getTags().stream())
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(blogs, blogMapper.toBlogResponseDTOs(blogs.getContent(), tags));
    }


    @Override
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogDetails(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        if (blog.getCurrentSubmission() == null) {
            throw new InvalidOperationException("Blog does not have a current submission.");
        }

        Set<Long> tagIds = blog.getCurrentSubmission().getTags().stream()
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return blogMapper.toBlogResponseDTO(blog, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogSubmissionResponseDTO> getBlogSubmissionHistory(Long blogId, PaginationRequestDTO paginationRequestDTO) {
        blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, BlogSubmission.class);
        Page<BlogSubmission> submissions = blogSubmissionRepository.findByBlogIdOrderBySubmittedAtDesc(blogId, pageable);

        Set<Long> tagIds = submissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(submissions, blogMapper.toBlogSubmissionResponseDTOs(submissions.getContent(), tags));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogSubmissionResponseDTO> getPendingBlogSubmissionsForReview(PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, BlogSubmission.class);

        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();
        List<UserProfileDto> managedUsers = authServiceClient.getManagedUsers(managerId, token);
        List<Long> managedUserIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Page<BlogSubmission> pendingSubmissions = blogSubmissionRepository.findBySubmitterIdInAndStatus(managedUserIds, SubmissionStatus.PENDING, pageable);

        Set<Long> tagIds = pendingSubmissions.getContent().stream()
                .flatMap(submission -> submission.getTags().stream())
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(pendingSubmissions, blogMapper.toBlogSubmissionResponseDTOs(pendingSubmissions.getContent(), tags));
    }

    @Override
    @Transactional
    public BlogSubmissionResponseDTO reviewBlogSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO) {
        BlogSubmission submission = blogSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog Submission not found with ID: " + submissionId));

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
            throw new InvalidOperationException("Blog Submission is not in PENDING status and cannot be reviewed.");
        }

        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && !StringUtils.hasText(reviewDTO.getReviewerComment())) {
            throw new InvalidOperationException("Reviewer comment is required for rejecting a submission.");
        }
        if (reviewDTO.getStatus() == SubmissionStatusDTO.REJECTED && reviewDTO.getReviewerComment().length() < 10) {
            throw new InvalidOperationException("Reviewer comment must be at least 10 characters long for rejection.");
        }

        submission.setStatus(blogMapper.toSubmissionStatusEntity(reviewDTO.getStatus()));
        submission.setReviewerId(reviewerId);
        submission.setReviewedAt(Instant.now());
        submission.setReviewerComment(reviewDTO.getReviewerComment());

        BlogSubmission updatedSubmission = blogSubmissionRepository.save(submission);

        if (reviewDTO.getStatus() == SubmissionStatusDTO.APPROVED) {
            Blog blog = updatedSubmission.getBlog();
            if (blog.getCurrentSubmission() == null || !blog.getCurrentSubmission().equals(updatedSubmission) || blog.getCurrentSubmission().getStatus() != SubmissionStatus.APPROVED) {
                blog.setCurrentSubmission(updatedSubmission);
                blogRepository.save(blog);
            }
        }

        sendNotification(submission, submission.getSubmitterId(), reviewerId);

        Set<Long> tagIds = updatedSubmission.getTags().stream()
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(tagIds), JwtUserContext.getToken());

        return blogMapper.toBlogSubmissionResponseDTO(updatedSubmission, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogResponseDTO> getAllApprovedAndActiveBlogs(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PaginationUtils.toPageable(paginationRequestDTO, Blog.class);

        Specification<Blog> spec = (root, query, cb) -> {
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
                Join<Blog, BlogSubmission> submissionJoin = root.join("currentSubmission");
                Join<BlogSubmission, BlogSubmissionTag> submissionTagJoin = submissionJoin.join("tags");
                predicates.add(submissionTagJoin.get("tagId").in(tagIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Blog> blogs = blogRepository.findAll(spec, pageable);

        Set<Long> uniqueTagIds = blogs.getContent().stream()
                .filter(blog -> blog.getCurrentSubmission() != null)
                .flatMap(blog -> blog.getCurrentSubmission().getTags().stream())
                .map(BlogSubmissionTag::getTagId)
                .collect(Collectors.toSet());

        List<TagResponseDto> tags = Collections.emptyList();
        if (!tagIds.isEmpty())
            tags = tagServiceClient.findAllTagsByIds(new ArrayList<>(uniqueTagIds), JwtUserContext.getToken());

        return PaginationUtils.mapToPaginationResponseDTO(blogs, blogMapper.toBlogResponseDTOs(blogs.getContent(), tags));
    }

    private void sendNotification(BlogSubmission submission, Long ownerId, Long actorId) {
        String title = getNotificationTitle(submission);
        com.edp.shared.kafka.model.SubmissionStatus notificationStatus = switch (submission.getStatus()) {
            case APPROVED -> com.edp.shared.kafka.model.SubmissionStatus.APPROVED;
            case REJECTED -> com.edp.shared.kafka.model.SubmissionStatus.REJECTED;
            default -> com.edp.shared.kafka.model.SubmissionStatus.PENDING;
        };

        NotificationDetails notificationDetails = NotificationDetails
                .builder()
                .title(title)
                .ownerId(ownerId)
                .actorId(actorId)
                .createdAt(Instant.now())
                .submissionType(SubmissionType.BLOG)
                .submissionId(submission.getBlog().getId())
                .status(notificationStatus).build();
        kafkaProducer.sendNotification(notificationDetails);
    }

    private static String getNotificationTitle(BlogSubmission submission) {
        String truncatedTitle = submission.getTitle().length() > 20
                ? submission.getTitle().substring(0, 20) + "..."
                : submission.getTitle();

        return switch (submission.getStatus()) {
            case PENDING -> "A blog submission titled '" + truncatedTitle + "' is pending your review";
            case APPROVED -> "Your blog submission titled '" + truncatedTitle + "' is approved by your manager";
            case REJECTED -> "Your blog submission titled '" + truncatedTitle + "' is rejected by your manager";
        };
    }
}