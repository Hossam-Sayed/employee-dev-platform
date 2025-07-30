package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;

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
        name = "Template Sections (Admin)",
        description = "Attach, remove or list sections in a specific template"
)
@Validated
@RequestMapping("/api/admin/templates/{templateId}/sections")
public interface TemplateSectionControllerApi {

    @Operation(
            summary = "List sections in template",
            description = "Returns all sections attached to the given template."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    @GetMapping
    ResponseEntity<List<TemplateSectionResponseDto>> listSections(
            @PathVariable Long templateId
    );

    @Operation(
            summary = "Attach section to template",
            description = "Attaches a new section to a template."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Section attached successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Template or Section not found"),
            @ApiResponse(responseCode = "409", description = "Section already attached to template")
    })
    @PostMapping
    ResponseEntity<TemplateSectionResponseDto> addSection(
            @PathVariable Long templateId,
            @Valid @RequestBody TemplateSectionRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Detach section from template",
            description = "Removes a section from the template."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Section detached successfully"),
            @ApiResponse(responseCode = "404", description = "Template section not found")
    })
    @DeleteMapping("/{templateSectionId}")
    ResponseEntity<Void> removeSection(
            @PathVariable Long templateId,
            @PathVariable Long templateSectionId
    );
}
