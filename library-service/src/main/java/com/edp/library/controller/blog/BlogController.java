package com.edp.library.controller.blog;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.shared.error.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Blog Management", description = "APIs for users to submit blog articles and for managers to review them.")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/blogs")
public interface BlogController {

    @Operation(summary = "Create a new blog submission",
            description = "Allows an author to submit a new blog article for review. " +
                    "A new Blog entity will be created along with its first PENDING submission. " +
                    "The authorId header is required. The reviewerId will be assigned based on internal business logic (e.g., default reviewer, author's manager).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog submitted successfully",
                    content = @Content(schema = @Schema(implementation = BlogResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "One or more provided tags not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A blog submission with the same title and document URL already exists for this author.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<BlogResponseDTO> createBlog(
            @Valid @RequestBody BlogCreateRequestDTO request,
            @Parameter(description = "ID of the author submitting the blog material", required = true)
            @RequestHeader("X-Author-Id") Long authorId,
            @RequestHeader("X-Reviewer-Id") Long reviewerId
    );

    @Operation(summary = "Resubmit a rejected blog",
            description = "Allows an author to resubmit a previously REJECTED blog. " +
                    "A new PENDING submission will be created for the existing Blog entity. " +
                    "Requires authorId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog resubmitted successfully",
                    content = @Content(schema = @Schema(implementation = BlogResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload, validation errors, or blog is not in REJECTED status",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Blog material or tags not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to edit this blog material",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{blogId}/resubmit")
    ResponseEntity<BlogResponseDTO> editRejectedBlogSubmission(
            @Parameter(description = "ID of the blog to resubmit", required = true) @PathVariable Long blogId,
            @Valid @RequestBody BlogCreateRequestDTO request,
            @Parameter(description = "ID of the author resubmitting the blog", required = true)
            @RequestHeader("X-Author-Id") Long authorId,
            @RequestHeader("X-Reviewer-Id") Long reviewerId
    );

    @Operation(summary = "Get my blogs",
            description = "Retrieves a paginated list of blogs owned by the authenticated author. " +
                    "Can be filtered by current submission status and/or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved blogs",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status filter or pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my-blogs")
    ResponseEntity<PaginationResponseDTO<BlogResponseDTO>> getMyBlogs(
            @Parameter(description = "ID of the author whose blogs are to be retrieved", required = true)
            @RequestHeader("X-Author-Id") Long authorId,
            @Parameter(description = "Optional filter for current submission status (PENDING, APPROVED, REJECTED)")
            @RequestParam(required = false) String statusFilter,
            @Parameter(description = "Optional filter for a specific tag ID")
            @RequestParam(required = false) Long tagIdFilter,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get details of a specific blog",
            description = "Retrieves full details of a specific blog, including its current submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved blog details",
                    content = @Content(schema = @Schema(implementation = BlogResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Blog not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{blogId}")
    ResponseEntity<BlogResponseDTO> getBlogDetails(
            @Parameter(description = "ID of the blog", required = true) @PathVariable Long blogId
    );

    @Operation(summary = "Get submission history for a blog",
            description = "Retrieves a paginated list of all past and current submissions for a specific blog, ordered by submission date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved submission history",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Blog not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{blogId}/history")
    ResponseEntity<PaginationResponseDTO<BlogSubmissionResponseDTO>> getBlogSubmissionHistory(
            @Parameter(description = "ID of the blog whose submission history is to be retrieved", required = true) @PathVariable Long blogId,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get pending blog submissions for review",
            description = "Retrieves a paginated list of blog submissions that are in PENDING status and assigned to a specific manager for review. Requires reviewerId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending submissions",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/submissions/pending-review")
    ResponseEntity<PaginationResponseDTO<BlogSubmissionResponseDTO>> getPendingBlogSubmissionsForReview(
            @Parameter(description = "ID of the manager/reviewer", required = true)
            @RequestHeader("X-Reviewer-Id") Long reviewerId,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Review a blog submission",
            description = "Allows a manager to approve or reject a PENDING blog submission. " +
                    "A reviewer comment is required if the submission is rejected and must be at least 10 characters long. " +
                    "If approved, the blog's current submission will be updated. Requires reviewerId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog submission reviewed successfully",
                    content = @Content(schema = @Schema(implementation = BlogSubmissionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review status, missing/too short reviewer comment for rejection, or submission is not pending",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Blog submission not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Reviewer is not authorized to review this submission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/submissions/{submissionId}/review")
    ResponseEntity<BlogSubmissionResponseDTO> reviewBlogSubmission(
            @Parameter(description = "ID of the blog submission to review", required = true) @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionReviewRequestDTO reviewDTO,
            @Parameter(description = "ID of the manager/reviewer performing the review", required = true)
            @RequestHeader("X-Reviewer-Id") Long reviewerId
    );

    @Operation(summary = "Get all approved and active blogs",
            description = "Retrieves a paginated list of all APPROVED blog articles. " +
                    "Can be searched by title/description keyword and/or filtered by tags.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved approved blogs",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/approved")
    ResponseEntity<PaginationResponseDTO<BlogResponseDTO>> getAllApprovedAndActiveBlogs(
            @Parameter(description = "Optional keyword to search in blog title or description (case-insensitive partial match)")
            @RequestParam(required = false) String searchKeyword,
            @Parameter(description = "Optional list of tag IDs to filter by. Blogs must have at least one of these tags.")
            @RequestParam(required = false) List<Long> tagIds,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );
}