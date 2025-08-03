package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.submission.SubmissionResponseDto;
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
}
