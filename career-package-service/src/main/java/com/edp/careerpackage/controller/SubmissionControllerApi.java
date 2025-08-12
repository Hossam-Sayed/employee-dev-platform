package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionSnapshotResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Submission",
        description = "Manage career package submissions"
)
@Validated
@RequestMapping("/api/submissions")
public interface SubmissionControllerApi {

    @Operation(
            summary = "Submit current career package",
            description = "Submits the current user's completed career package."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Submission created and package marked as SUBMITTED"),
            @ApiResponse(responseCode = "400", description = "Submission not allowed: progress not complete or invalid status"),
            @ApiResponse(responseCode = "404", description = "Career package not found")
    })
    @PostMapping
    ResponseEntity<SubmissionResponseDto> submitCareerPackage();

    @Operation(
            summary = "Get all previous submissions",
            description = "Returns a list of the current user's submission history"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions fetched successfully")
    })
    @GetMapping
    ResponseEntity<List<SubmissionResponseDto>> getSubmissionHistory();


    @Operation(
            summary = "Search submissions by user IDs (MANAGER only)",
            description = "Returns a list of submissions belonging to the given user IDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Only managers can access this endpoint")
    })
    @GetMapping("/search")
    ResponseEntity<List<SubmissionResponseDto>> searchSubmissions();


    @Operation(
            summary = "Approve a submission (MANAGER only)",
            description = "Approve a submission and mark it as APPROVED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission approved"),
            @ApiResponse(responseCode = "403", description = "Only managers can approve submissions"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PutMapping("/{submissionId}/approve")
    ResponseEntity<SubmissionResponseDto> approveSubmission(
            @PathVariable Long submissionId,
            @RequestBody CommentRequestDto commentRequest
    );


    @Operation(
            summary = "Reject a submission (MANAGER only)",
            description = "Reject a submission and mark it as REJECTED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission rejected"),
            @ApiResponse(responseCode = "403", description = "Only managers can reject submissions"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PutMapping("/{submissionId}/reject")
    ResponseEntity<SubmissionResponseDto> rejectSubmission(
            @PathVariable Long submissionId,
            @RequestBody CommentRequestDto commentRequest
    );

    @Operation(
            summary = "Get a detailed submission by ID",
            description = "Returns a detailed view of a specific submission, including user info, sections, and tag snapshots."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Submission not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view this submission")
    })
    @GetMapping("/{submissionId}")
    ResponseEntity<SubmissionSnapshotResponseDto> getSubmissionDetails(@PathVariable Long submissionId);
}
