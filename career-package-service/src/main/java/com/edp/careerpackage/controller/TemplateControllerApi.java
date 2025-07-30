package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(
        name = "Templates",
        description = "Manage career package templates by department & position"
)
@Validated
@RequestMapping("/api/templates")
public interface TemplateControllerApi {

    @GetMapping
    ResponseEntity<Page<TemplateResponseDto>> listTemplates(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/{id}")
    ResponseEntity<TemplateDetailResponseDto> getTemplateById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<TemplateDetailResponseDto> createTemplate(
            @Valid @RequestBody TemplateRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @PutMapping("/{id}")
    ResponseEntity<TemplateDetailResponseDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateUpdateRequestDto request
    );

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTemplate(@PathVariable Long id);

    @Operation(
            summary = "List sections in template",
            description = "Returns all sections attached to the given template."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    @GetMapping("/{templateId}/sections")
    ResponseEntity<List<TemplateSectionResponseDto>> listSections(@PathVariable Long templateId);
}
