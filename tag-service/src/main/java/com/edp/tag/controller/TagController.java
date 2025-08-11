package com.edp.tag.controller;

import com.edp.tag.model.tag.TagRequestDto;
import com.edp.tag.model.tag.TagResponseDto;

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
public interface TagController {

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

    @Operation(
            summary = "Fetch tag by ID",
            description = "Fetch tag by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Tag doesn't exist")
    })
    @GetMapping("/{id}")
    ResponseEntity<TagResponseDto> findTagById(@PathVariable("id") Long tagId);


    @Operation(
            summary = "Fetch tags by IDs",
            description = "Fetch tags by IDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tags fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "No tag exist")
    })
    @GetMapping("/by-ids")
    ResponseEntity<List<TagResponseDto>> findAllTagsByIds(@RequestParam("ids") List<Long> tagIds);
}
