package com.edp.library.controller.tag;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.tag.TagCreateRequestDTO;
import com.edp.library.model.tag.TagDTO;
import com.edp.library.model.tag.TagRequestResponseDTO;
import com.edp.library.model.tag.TagRequestReviewDTO;
import com.edp.library.model.tag.TagUpdateStatusDTO;
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

@Tag(name = "Tag Management", description = "APIs for managing tags, including user requests and admin operations.")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/tags")
public interface TagController {

    @Operation(summary = "Submit a new tag request by a user",
            description = "Allows any user to request the creation of a new tag. The request will be in PENDING status for admin review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag request submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or tag name is empty", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A tag with the requested name already exists (approved, pending, or rejected)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/requests")
    ResponseEntity<TagRequestResponseDTO> createTagRequest(
            @Valid @RequestBody TagCreateRequestDTO request
    );

    @Operation(summary = "Get a user's tag requests",
            description = "Retrieves a paginated list of tag requests made by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's tag requests"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my-requests")
    ResponseEntity<PaginationResponseDTO<TagRequestResponseDTO>> getMyTagRequests(
            @Parameter(description = "ID of the user whose tag requests are to be retrieved") @RequestHeader("X-Requester-Id") Long requesterId,
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Get all pending tag requests for admin review",
            description = "Retrieves a paginated list of all tag requests that are in PENDING status, awaiting administrative review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending tag requests"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/requests/pending")
    ResponseEntity<PaginationResponseDTO<TagRequestResponseDTO>> getAllPendingTagRequests(
            @Valid @Parameter(description = "Pagination and sorting parameters") PaginationRequestDTO paginationRequestDTO
    );

    @Operation(summary = "Review a tag request",
            description = "Allows an administrator to approve or reject a pending tag request. " +
                    "If approved, a new active tag is created. If rejected, a reviewer comment is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag request reviewed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review status or missing reviewer comment for rejection", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag request not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Tag request is not pending, or an active tag with the same name already exists on approval attempt", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/requests/{tagRequestId}/review")
    ResponseEntity<TagRequestResponseDTO> reviewTagRequest(
            @Parameter(description = "ID of the tag request to review") @PathVariable Long tagRequestId,
            @Valid @RequestBody TagRequestReviewDTO reviewDTO,
            @Parameter(description = "ID of the administrator performing the review") @RequestHeader("X-Reviewer-Id") Long reviewerId
    );
}