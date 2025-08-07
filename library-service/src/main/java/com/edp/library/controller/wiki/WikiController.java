package com.edp.library.controller.wiki;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.wiki.WikiCreateRequestDTO;
import com.edp.library.model.wiki.WikiResponseDTO;
import com.edp.library.model.wiki.WikiSubmissionResponseDTO;
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

@Tag(name = "Wiki Management", description = "APIs for users to submit wiki articles and for managers to review them.")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/wikis")
public interface WikiController {

    @Operation(summary = "Create a new wiki submission",
            description = "Allows an author to submit a new wiki article for review. " +
                    "A new Wiki entity will be created along with its first PENDING submission. " +
                    "The authorId header is required. The reviewerId will be assigned based on internal business logic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Wiki submitted successfully",
                    content = @Content(schema = @Schema(implementation = WikiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "One or more provided tags not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A wiki submission with the same title and document URL already exists for this author.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<WikiResponseDTO> createWiki(
            @Valid @RequestBody WikiCreateRequestDTO request
    );

    @Operation(summary = "Resubmit a rejected wiki",
            description = "Allows an author to resubmit a previously REJECTED wiki. " +
                    "A new PENDING submission will be created for the existing Wiki entity. " +
                    "Requires authorId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wiki resubmitted successfully",
                    content = @Content(schema = @Schema(implementation = WikiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload, validation errors, or wiki is not in REJECTED status",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Wiki material or tags not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to edit this wiki material",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{wikiId}/resubmit")
    ResponseEntity<WikiResponseDTO> editRejectedWikiSubmission(
            @Parameter(description = "ID of the wiki to resubmit", required = true) @PathVariable Long wikiId,
            @Valid @RequestBody WikiCreateRequestDTO request
    );

    @Operation(summary = "Get my wikis",
            description = "Retrieves a paginated list of wikis owned by the authenticated author. " +
                    "Can be filtered by current submission status and/or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved wikis",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status filter or pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my-wikis")
    ResponseEntity<PaginationResponseDTO<WikiResponseDTO>> getMyWikis(
            @Parameter(description = "Optional filter for current submission status (PENDING, APPROVED, REJECTED)")
            @RequestParam(required = false) String statusFilter,
            @Parameter(description = "Optional filter for a specific tag ID")
            @RequestParam(required = false) Long tagIdFilter,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get details of a specific wiki",
            description = "Retrieves full details of a specific wiki, including its current submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved wiki details",
                    content = @Content(schema = @Schema(implementation = WikiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Wiki not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{wikiId}")
    ResponseEntity<WikiResponseDTO> getWikiDetails(
            @Parameter(description = "ID of the wiki", required = true) @PathVariable Long wikiId
    );

    @Operation(summary = "Get submission history for a wiki",
            description = "Retrieves a paginated list of all past and current submissions for a specific wiki, ordered by submission date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved submission history",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Wiki not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{wikiId}/history")
    ResponseEntity<PaginationResponseDTO<WikiSubmissionResponseDTO>> getWikiSubmissionHistory(
            @Parameter(description = "ID of the wiki whose submission history is to be retrieved", required = true) @PathVariable Long wikiId,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get pending wiki submissions for review",
            description = "Retrieves a paginated list of wiki submissions that are in PENDING status and assigned to a specific manager for review. Requires reviewerId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending submissions",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/submissions/pending-review")
    ResponseEntity<PaginationResponseDTO<WikiSubmissionResponseDTO>> getPendingWikiSubmissionsForReview(
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Review a wiki submission",
            description = "Allows a manager to approve or reject a PENDING wiki submission. " +
                    "A reviewer comment is required if the submission is rejected and must be at least 10 characters long. " +
                    "If approved, the wiki's current submission will be updated. Requires reviewerId header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wiki submission reviewed successfully",
                    content = @Content(schema = @Schema(implementation = WikiSubmissionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review status, missing/too short reviewer comment for rejection, or submission is not pending",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Wiki submission not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Reviewer is not authorized to review this submission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/submissions/{submissionId}/review")
    ResponseEntity<WikiSubmissionResponseDTO> reviewWikiSubmission(
            @Parameter(description = "ID of the wiki submission to review", required = true) @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionReviewRequestDTO reviewDTO
    );

    @Operation(summary = "Get all approved and active wikis",
            description = "Retrieves a paginated list of all APPROVED wiki articles. " +
                    "Can be searched by title/description keyword and/or filtered by tags.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved approved wikis",
                    content = @Content(schema = @Schema(implementation = PaginationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/approved")
    ResponseEntity<PaginationResponseDTO<WikiResponseDTO>> getAllApprovedAndActiveWikis(
            @Parameter(description = "Optional keyword to search in wiki title or description (case-insensitive partial match)")
            @RequestParam(required = false) String searchKeyword,
            @Parameter(description = "Optional list of tag IDs to filter by. Wikis must have at least one of these tags.")
            @RequestParam(required = false) List<Long> tagIds,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );
}