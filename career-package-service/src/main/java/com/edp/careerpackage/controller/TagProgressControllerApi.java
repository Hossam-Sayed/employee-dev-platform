package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.tagprogress.TagProgressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Tag Progress",
        description = "Update tag progress for a user's career package"
)
@Validated
@RequestMapping("/api/tag-progress")
public interface TagProgressControllerApi {

    @Operation(
            summary = "Update tag progress",
            description = "Updates the completed value and proof URL for a given tag progress"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag progress updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid progress or proof data"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this tag progress"),
            @ApiResponse(responseCode = "404", description = "Tag progress not found")
    })
    @PutMapping("/{tagProgressId}")
    ResponseEntity<TagProgressResponseDto> updateTagProgress(
            @Parameter(description = "ID of the tag progress to update", required = true)
            @PathVariable Long tagProgressId,

            @Valid @RequestBody TagProgressRequestDto request
    );
}
