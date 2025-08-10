package com.edp.library.service.blog;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {

    /**
     * User Story: As a User, I want to submit a new blog so that it can be reviewed and published.
     * <p>
     * Creates a new blog entry with its initial submission in PENDING status.
     * Validates that all provided tags are active.
     * A new Blog entity is created, and the first BlogSubmission is linked as its current submission.
     *
     * @param request The DTO containing the blog's title, description, document URL, and tag IDs.
     * @param file    The blog file.
     * @return A {@link BlogResponseDTO} representing the newly created blog and its current submission.
     * @throws com.edp.library.exception.ResourceNotFoundException      if any provided tag ID does not exist.
     * @throws com.edp.library.exception.InvalidOperationException      if any provided tag is not active.
     * @throws com.edp.library.exception.ResourceAlreadyExistsException if a blog submission with the same title and document URL already exists for this author.
     */
    BlogResponseDTO createBlog(BlogCreateRequestDTO request, MultipartFile file);

    /**
     * User Story: As a User, I want to edit and resubmit a rejected blog so that it can be reviewed again.
     * <p>
     * Creates a new submission for an existing blog that currently has a REJECTED status.
     * The previous rejected submission remains in history, and the new submission becomes the current one (PENDING).
     * Validates that the requesting user is the author and that all provided tags are active.
     *
     * @param blogId  The ID of the blog to be resubmitted.
     * @param request The DTO containing the updated blog details (title, description, document URL, tag IDs).
     * @param file    The blog file.
     * @return A {@link BlogResponseDTO} representing the blog with its new current (PENDING) submission.
     * @throws com.edp.library.exception.ResourceNotFoundException if the blog or any provided tag is not found.
     * @throws com.edp.library.exception.InvalidOperationException if the blog's current submission is not REJECTED, or if the authorId does not match.
     */
    BlogResponseDTO editRejectedBlogSubmission(Long blogId, BlogCreateRequestDTO request, MultipartFile file);

    /**
     * User Story: As a User, I want to see a list of my submitted blogs.
     * User Story: As a User, I want to filter my blogs by status and tags.
     * <p>
     * Retrieves a paginated list of blogs owned by a specific author.
     * Can be filtered by the status of the current submission (e.g., APPROVED, PENDING, REJECTED)
     * and/or by a specific tag ID associated with the current submission.
     *
     * @param statusFilter         Optional filter for the current submission's status.
     * @param tagIdFilter          Optional filter for a specific tag ID associated with the current submission.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link BlogResponseDTO}s.
     * @throws IllegalArgumentException if an invalid statusFilter value is provided.
     */
    PaginationResponseDTO<BlogResponseDTO> getMyBlogs(String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a User, I want to view the details of a specific blog.
     * <p>
     * Retrieves the complete details of a single blog, including its current submission and associated tags.
     *
     * @param blogId The ID of the blog to retrieve.
     * @return A {@link BlogResponseDTO} containing the blog's details.
     * @throws com.edp.library.exception.ResourceNotFoundException if the blog with the given ID is not found.
     */
    BlogResponseDTO getBlogDetails(Long blogId);

    /**
     * User Story: As a User, I want to see the submission history of a blog.
     * <p>
     * Retrieves a paginated list of all submissions (past and current) for a specific blog,
     * ordered from most recent to oldest.
     *
     * @param blogId               The ID of the blog whose submission history is to be retrieved.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link BlogSubmissionResponseDTO}s.
     * @throws com.edp.library.exception.ResourceNotFoundException if the blog with the given ID is not found.
     */
    PaginationResponseDTO<BlogSubmissionResponseDTO> getBlogSubmissionHistory(Long blogId, PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a Manager, I want to see all blogs pending my review.
     * <p>
     * Retrieves a paginated list of blog submissions that are in PENDING status and assigned to a specific reviewer.
     *
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link BlogSubmissionResponseDTO}s.
     */
    PaginationResponseDTO<BlogSubmissionResponseDTO> getPendingBlogSubmissionsForReview(PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a Manager, I want to approve or reject a blog submission.
     * <p>
     * Reviews a specific blog submission, changing its status to APPROVED or REJECTED.
     * Requires the reviewer to be assigned to the submission and the submission to be in PENDING status.
     * A reviewer comment is mandatory for rejection and must meet a minimum length.
     * If approved, the associated Blog entity's current submission is updated.
     *
     * @param submissionId The ID of the blog submission to review.
     * @param reviewDTO    The DTO containing the new status (APPROVED/REJECTED) and an optional reviewer comment.
     * @return A {@link BlogSubmissionResponseDTO} representing the updated submission.
     * @throws com.edp.library.exception.ResourceNotFoundException if the submission is not found.
     * @throws com.edp.library.exception.InvalidOperationException if the reviewer is not authorized,
     *                                                             if the submission is not PENDING,
     *                                                             or if a rejection comment is missing/too short.
     */
    BlogSubmissionResponseDTO reviewBlogSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO);

    /**
     * User Story: As a User, I want to search for approved blogs by title or description.
     * User Story: As a User, I want to filter approved blogs by tags.
     * <p>
     * Retrieves a paginated list of all APPROVED and ACTIVE blogs.
     * Can be filtered by title/description (case-insensitive partial match) and/or by tag IDs.
     *
     * @param searchKeyword        Optional keyword to search in title or description.
     * @param tagIds               Optional list of tag IDs to filter by. Blogs must have at least one of these tags.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link BlogResponseDTO}s.
     */
    PaginationResponseDTO<BlogResponseDTO> getAllApprovedAndActiveBlogs(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO);
}