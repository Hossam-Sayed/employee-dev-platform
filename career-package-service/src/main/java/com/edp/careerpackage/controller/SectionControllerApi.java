package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.section.SectionRequestDto;
import com.edp.careerpackage.model.section.SectionResponseDto;

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
        name = "Sections (Admin)",
        description = "Lookup and create sections used in templates"
)
@Validated
@RequestMapping("/api/admin/sections")
public interface SectionControllerApi {

    @Operation(
            summary = "Search sections",
            description = "Returns sections filtered by name."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections fetched successfully")
    })
    @GetMapping
    ResponseEntity<List<SectionResponseDto>> searchSections(
            @RequestParam(required = false) String query
    );

    @Operation(
            summary = "Create section",
            description = "Creates a new section by name and description."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Section created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Section with the same name already exists")
    })
    @PostMapping
    ResponseEntity<SectionResponseDto> createSection(
            @Valid @RequestBody SectionRequestDto request,
            UriComponentsBuilder uriBuilder
    );
}
