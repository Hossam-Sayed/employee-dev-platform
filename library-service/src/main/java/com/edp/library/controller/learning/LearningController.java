package com.edp.library.controller.learning;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;
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

@Tag(name = "Learning Management", description = "APIs for users to submit learning materials and for managers to review them.")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/learnings")
public interface LearningController {

    @Operation(summary = "Create a new learning material submission",
            description = "Allows an employee to submit a new learning material for review. " +
                    "A new Learning entity will be created along with its first PENDING submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Learning material submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation errors", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "One or more provided tags not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A learning submission with the same title and proof URL already exists for this employee", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<LearningResponseDTO> createLearning(
            @Valid @RequestBody LearningCreateRequestDTO request
    );

    @Operation(summary = "Resubmit a rejected learning material",
            description = "Allows an employee to resubmit a previously REJECTED learning material. " +
                    "A new PENDING submission will be created for the existing Learning entity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Learning material resubmitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload, validation errors, or learning material is not in REJECTED status", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Learning material or tags not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to edit this learning material", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{learningId}/resubmit")
    ResponseEntity<LearningResponseDTO> editRejectedLearningSubmission(
            @Parameter(description = "ID of the learning material to resubmit", required = true) @PathVariable Long learningId,
            @Valid @RequestBody LearningCreateRequestDTO request
    );

    @Operation(summary = "Get my learning materials",
            description = "Retrieves a paginated list of learning materials owned by the authenticated employee. " +
                    "Can be filtered by current submission status and/or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved learning materials"),
            @ApiResponse(responseCode = "400", description = "Invalid status filter or pagination parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my-learnings")
    ResponseEntity<PaginationResponseDTO<LearningResponseDTO>> getMyLearnings(
            @Parameter(description = "Optional filter for current submission status (PENDING, APPROVED, REJECTED)") @RequestParam(required = false) String statusFilter,
            @Parameter(description = "Optional filter for a specific tag ID") @RequestParam(required = false) Long tagIdFilter,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get details of a specific learning material",
            description = "Retrieves full details of a specific learning material, including its current submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved learning material details"),
            @ApiResponse(responseCode = "404", description = "Learning material not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{learningId}")
    ResponseEntity<LearningResponseDTO> getLearningDetails(
            @Parameter(description = "ID of the learning material", required = true) @PathVariable Long learningId
    );

    @Operation(summary = "Get submission history for a learning material",
            description = "Retrieves a paginated list of all past and current submissions for a specific learning material, ordered by submission date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved submission history"),
            @ApiResponse(responseCode = "404", description = "Learning material not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{learningId}/history")
    ResponseEntity<PaginationResponseDTO<LearningSubmissionResponseDTO>> getLearningSubmissionHistory(
            @Parameter(description = "ID of the learning material whose submission history is to be retrieved", required = true) @PathVariable Long learningId,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get pending learning submissions for review",
            description = "Retrieves a paginated list of learning submissions that are in PENDING status and assigned to a specific manager for review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending submissions"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/submissions/pending-review")
    ResponseEntity<PaginationResponseDTO<LearningSubmissionResponseDTO>> getPendingLearningSubmissionsForReview(
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Review a learning submission",
            description = "Allows a manager to approve or reject a PENDING learning submission. " +
                    "A reviewer comment is required if the submission is rejected. " +
                    "If approved, the learning material's current submission will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Learning submission reviewed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review status, missing reviewer comment for rejection, or submission is not pending", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Learning submission not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Reviewer is not authorized to review this submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/submissions/{submissionId}/review")
    ResponseEntity<LearningSubmissionResponseDTO> reviewLearningSubmission(
            @Parameter(description = "ID of the learning submission to review", required = true) @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionReviewRequestDTO reviewDTO
    );
}