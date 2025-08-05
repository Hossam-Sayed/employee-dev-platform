package com.edp.library.controller.blog;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.library.service.blog.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogControllerImpl implements BlogController {

    private final BlogService blogService;

    // TODO: Determine reviewerId internally by business logic, not client provided

    // TODO: For reviewers Endpoints
    //  Authentication system would ideally ensure the current user is a reviewer/manager
    //  and map their ID to 'reviewerId' or validate that 'X-Reviewer-Id' matches their ID.

    @Override
    public ResponseEntity<BlogResponseDTO> createBlog(
            BlogCreateRequestDTO request,
            Long authorId,
            Long reviewerId
    ) {
        BlogResponseDTO response = blogService.createBlog(request, authorId, reviewerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BlogResponseDTO> editRejectedBlogSubmission(
            Long blogId,
            BlogCreateRequestDTO request,
            Long authorId,
            Long reviewerId
    ) {
        BlogResponseDTO response = blogService.editRejectedBlogSubmission(blogId, request, authorId, reviewerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<BlogResponseDTO>> getMyBlogs(
            Long authorId,
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<BlogResponseDTO> response = blogService.getMyBlogs(authorId, statusFilter, tagIdFilter, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BlogResponseDTO> getBlogDetails(Long blogId) {
        BlogResponseDTO response = blogService.getBlogDetails(blogId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<BlogSubmissionResponseDTO>> getBlogSubmissionHistory(
            Long blogId,
            PaginationRequestDTO paginationRequestDTO
    ) {

        PaginationResponseDTO<BlogSubmissionResponseDTO> response = blogService.getBlogSubmissionHistory(blogId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<BlogSubmissionResponseDTO>> getPendingBlogSubmissionsForReview(
            Long reviewerId,
            PaginationRequestDTO paginationRequestDTO
    ) {

        PaginationResponseDTO<BlogSubmissionResponseDTO> response = blogService.getPendingBlogSubmissionsForReview(reviewerId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BlogSubmissionResponseDTO> reviewBlogSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO,
            Long reviewerId
    ) {
        BlogSubmissionResponseDTO response = blogService.reviewBlogSubmission(submissionId, reviewDTO, reviewerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<BlogResponseDTO>> getAllApprovedAndActiveBlogs(
            String searchKeyword,
            List<Long> tagIds,
            PaginationRequestDTO paginationRequestDTO
    ) {

        PaginationResponseDTO<BlogResponseDTO> response = blogService.getAllApprovedAndActiveBlogs(searchKeyword, tagIds, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }
}