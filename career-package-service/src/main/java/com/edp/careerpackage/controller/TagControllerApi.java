package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.tag.TagRequestDto;
import com.edp.careerpackage.model.tag.TagResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(
        name = "Tags",
        description = "Lookup and create tags used in templates"
)
@Validated
@RequestMapping("/api/tags")
public interface TagControllerApi {

    @Operation(
            summary = "Search tags",
            description = "Returns tags filtered by name."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tags fetched successfully")
    })
    @GetMapping
    ResponseEntity<List<TagResponseDto>> searchTags(
            @RequestParam(required = false) String query
    );

    @Operation(
            summary = "Create tag",
            description = "Creates a new tag."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Tag with the same name already exists")
    })
    @PostMapping
    ResponseEntity<TagResponseDto> createTag(
            @Valid @RequestBody TagRequestDto request,
            UriComponentsBuilder uriBuilder
    );
}
