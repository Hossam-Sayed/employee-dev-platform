package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

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
        name = "Template Sections",
        description = "Attach or detach sections from templates and view required tags"
)
@Validated
@RequestMapping("/api/template-sections")
public interface TemplateSectionControllerApi {

    @Operation(
            summary = "Attach section to template",
            description = "Attaches a section to a template"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Section attached successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Template or section not found"),
            @ApiResponse(responseCode = "409", description = "Section already attached")
    })
    @PostMapping
    ResponseEntity<TemplateSectionResponseDto> attachSection(
            @Valid @RequestBody TemplateSectionRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Detach section from template",
            description = "Detaches a section from a template"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Section detached successfully"),
            @ApiResponse(responseCode = "404", description = "TemplateSection not found")
    })
    @DeleteMapping("/{templateSectionId}")
    ResponseEntity<Void> detachSection(@PathVariable Long templateSectionId);

    @Operation(
            summary = "List required tags for a template section",
            description = "Returns all required tags attached to a given template section"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Required tags fetched successfully"),
            @ApiResponse(responseCode = "404", description = "TemplateSection not found")
    })
    @GetMapping("/{templateSectionId}/required-tags")
    ResponseEntity<List<TemplateSectionRequiredTagResponseDto>> listRequiredTags(
            @PathVariable Long templateSectionId
    );
}
