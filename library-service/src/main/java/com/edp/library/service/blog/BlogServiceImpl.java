package com.edp.library.service.blog;

import com.edp.library.data.entity.blog.Blog;
import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.entity.blog.BlogSubmissionTag;
import com.edp.library.data.entity.tag.Tag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.data.repository.blog.BlogRepository;
import com.edp.library.data.repository.blog.BlogSubmissionRepository;
import com.edp.library.data.repository.blog.BlogSubmissionTagRepository;
import com.edp.library.data.repository.tag.TagRepository;
import com.edp.library.exception.InvalidOperationException;
import com.edp.library.exception.ResourceNotFoundException;
import com.edp.library.mapper.BlogMapper;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
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

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogSubmissionRepository blogSubmissionRepository;
    private final BlogSubmissionTagRepository blogSubmissionTagRepository;
    private final TagRepository tagRepository;
    private final BlogMapper blogMapper;

    @Override
    @Transactional
    public BlogResponseDTO createBlog(BlogCreateRequestDTO request, Long authorId, Long reviewerId) {
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

        // 3. Create a new Blog entity
        Blog blog = blogMapper.toBlog(authorId);
        blog = blogRepository.save(blog);

        // 4. Create and save the new BlogSubmission for this new Blog
        BlogSubmission submission = blogMapper.toBlogSubmission(request, blog, authorId, reviewerId);
        submission = blogSubmissionRepository.save(submission); // Save to get submission ID for tags

        // 5. Save BlogSubmissionTag entries
        Set<BlogSubmissionTag> submissionTags = new HashSet<>();
        for (Long tagId : request.getTagIds()) {
            Tag tag = activeTagsMap.get(tagId); // Get the Tag entity from the map
            BlogSubmissionTag blogSubmissionTag = BlogSubmissionTag.builder()
                    .blogSubmission(submission)
                    .tag(tag)
                    .build();
            submissionTags.add(blogSubmissionTag);
        }
        blogSubmissionTagRepository.saveAll(submissionTags);
        submission.setTags(submissionTags); // Link the tags to the submission entity

        // 6. Set the current submission for the newly created Blog
        blog.setCurrentSubmission(submission);
        blogRepository.save(blog);

        // TODO: Notification: As a USER, I need to receive notifications when my blog submission status changes.
        // TODO: Notification: As a MANAGER, I need to receive notifications when a new blog submission is assigned to me for review.

        return blogMapper.toBlogResponseDTO(blog);
    }

    @Override
    @Transactional
    public BlogResponseDTO editRejectedBlogSubmission(Long blogId, BlogCreateRequestDTO request, Long authorId, Long reviewerId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        if (!blog.getAuthorId().equals(authorId)) {
            throw new InvalidOperationException("User is not authorized to edit this blog material.");
        }

        if (blog.getCurrentSubmission() == null || blog.getCurrentSubmission().getStatus() != SubmissionStatus.REJECTED) {
            throw new InvalidOperationException("Only blogs with a REJECTED current submission can be edited directly via this method. Current status: " +
                    (blog.getCurrentSubmission() != null ? blog.getCurrentSubmission().getStatus() : "N/A"));
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

        // Create and save the new BlogSubmission for the existing Blog material
        BlogSubmission newSubmission = blogMapper.toBlogSubmission(request, blog, authorId, reviewerId);
        newSubmission = blogSubmissionRepository.save(newSubmission);

        // Save BlogSubmissionTag entries for the new submission
        Set<BlogSubmissionTag> newSubmissionTags = new HashSet<>();
        for (Long tagId : request.getTagIds()) {
            Tag tag = activeTagsMap.get(tagId); // Get the Tag entity from the map
            BlogSubmissionTag submissionTag = BlogSubmissionTag.builder()
                    .blogSubmission(newSubmission)
                    .tag(tag)
                    .build();
            newSubmissionTags.add(submissionTag);
        }
        blogSubmissionTagRepository.saveAll(newSubmissionTags);
        newSubmission.setTags(newSubmissionTags);

        // Update the Blog material's current submission to the newly created PENDING one
        blog.setCurrentSubmission(newSubmission);
        blogRepository.save(blog);

        // TODO: Notification: Similar to create, notify owner of new pending submission
        // TODO: Notification: Notify manager of new submission assigned for review

        return blogMapper.toBlogResponseDTO(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogResponseDTO> getMyBlogs(
            Long authorId,
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize()
        );

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


        Page<Blog> blogs = blogRepository.findAll(spec, pageable);
        return mapToPaginationResponseDTO(blogs, blogMapper.toBlogResponseDTOs(blogs.getContent()));
    }


    @Override
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogDetails(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));
        return blogMapper.toBlogResponseDTO(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogSubmissionResponseDTO> getBlogSubmissionHistory(Long blogId, PaginationRequestDTO paginationRequestDTO) {
        // Ensure the blog exists
        blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(paginationRequestDTO.getSortDirection(), getActualSortBy(paginationRequestDTO.getSortBy(), BlogSubmission.class))
        );
        Page<BlogSubmission> submissions = blogSubmissionRepository.findByBlogIdOrderBySubmittedAtDesc(blogId, pageable);
        return mapToPaginationResponseDTO(submissions, blogMapper.toBlogSubmissionResponseDTOs(submissions.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogSubmissionResponseDTO> getPendingBlogSubmissionsForReview(Long reviewerId, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(paginationRequestDTO.getSortDirection(), getActualSortBy(paginationRequestDTO.getSortBy(), BlogSubmission.class))
        );
        Page<BlogSubmission> pendingSubmissions = blogSubmissionRepository.findByReviewerIdAndStatus(reviewerId, SubmissionStatus.PENDING, pageable);
        return mapToPaginationResponseDTO(pendingSubmissions, blogMapper.toBlogSubmissionResponseDTOs(pendingSubmissions.getContent()));
    }

    @Override
    @Transactional
    public BlogSubmissionResponseDTO reviewBlogSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO, Long reviewerId) {
        BlogSubmission submission = blogSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog Submission not found with ID: " + submissionId));

        if (!submission.getReviewerId().equals(reviewerId)) {
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
        submission.setReviewedAt(Instant.now()); // Set reviewedAt manually or rely on @UpdateTimestamp if configured for it
        submission.setReviewerComment(reviewDTO.getReviewerComment());

        BlogSubmission updatedSubmission = blogSubmissionRepository.save(submission);

        if (reviewDTO.getStatus() == SubmissionStatusDTO.APPROVED) {
            Blog blog = updatedSubmission.getBlog();
            // Update the 'currentSubmission' for the Blog to point to this newly approved submission.
            // This is crucial for retrieving the latest approved version of the blog.
            if (blog.getCurrentSubmission() == null || !blog.getCurrentSubmission().equals(updatedSubmission) || blog.getCurrentSubmission().getStatus() != SubmissionStatus.APPROVED) {
                blog.setCurrentSubmission(updatedSubmission);
                blogRepository.save(blog);
            }
        }

        // TODO: Notification: Notify submitter about status change.
        // TODO: Notification: Notify reviewer of their performed action.

        return blogMapper.toBlogSubmissionResponseDTO(updatedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<BlogResponseDTO> getAllApprovedAndActiveBlogs(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO) {
        Pageable pageable = PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(paginationRequestDTO.getSortDirection(), getActualSortBy(paginationRequestDTO.getSortBy(), Blog.class))
        );

        Specification<Blog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filter for APPROVED blogs with a current submission
            predicates.add(cb.isNotNull(root.get("currentSubmission")));
            predicates.add(cb.equal(root.get("currentSubmission").get("status"), SubmissionStatus.APPROVED));

            if (StringUtils.hasText(searchKeyword)) {
                String likePattern = "%" + searchKeyword.toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("currentSubmission").get("title")), likePattern);
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("currentSubmission").get("description")), likePattern);
                predicates.add(cb.or(titlePredicate, descriptionPredicate));
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                Objects.requireNonNull(query).distinct(true); // Ensure distinct Blog entities are returned
                Join<Blog, BlogSubmission> submissionJoin = root.join("currentSubmission");
                Join<BlogSubmission, BlogSubmissionTag> submissionTagJoin = submissionJoin.join("tags");
                predicates.add(submissionTagJoin.get("tag").get("id").in(tagIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Blog> blogs = blogRepository.findAll(spec, pageable);
        return mapToPaginationResponseDTO(blogs, blogMapper.toBlogResponseDTOs(blogs.getContent()));
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

    // Helper to dynamically adjust sortBy field based on entity type for Pageable
    private String getActualSortBy(String requestedSortBy, Class<?> entityClass) {
        if ("createdAt".equalsIgnoreCase(requestedSortBy)) {
            // If the entity is a BlogSubmission, use 'submittedAt'
            if (BlogSubmission.class.isAssignableFrom(entityClass)) {
                return "submittedAt";
            }
            // If the entity is Blog, use 'createdAt' (assuming Blog has createdAt)
            return "createdAt"; // This is the default if not BlogSubmission
        }
        // For other requestedSortBy values, return as is
        return requestedSortBy;
    }
}