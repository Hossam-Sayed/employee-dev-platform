package com.edp.library.controller.blog;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.library.service.blog.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogControllerImpl implements BlogController {

    private final BlogService blogService;

    @Override
    public ResponseEntity<BlogResponseDTO> createBlog(
            BlogCreateRequestDTO request
    ) {
        BlogResponseDTO response = blogService.createBlog(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BlogResponseDTO> editRejectedBlogSubmission(
            Long blogId,
            BlogCreateRequestDTO request
    ) {
        BlogResponseDTO response = blogService.editRejectedBlogSubmission(blogId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<BlogResponseDTO>> getMyBlogs(
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<BlogResponseDTO> response = blogService.getMyBlogs(statusFilter, tagIdFilter, paginationRequestDTO);
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
            PaginationRequestDTO paginationRequestDTO
    ) {

        PaginationResponseDTO<BlogSubmissionResponseDTO> response = blogService.getPendingBlogSubmissionsForReview(paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BlogSubmissionResponseDTO> reviewBlogSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO
    ) {
        BlogSubmissionResponseDTO response = blogService.reviewBlogSubmission(submissionId, reviewDTO);
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