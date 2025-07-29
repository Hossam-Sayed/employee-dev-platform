package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.TemplateRequestDto;
import com.edp.careerpackage.model.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.TemplateResponseDto;
import com.edp.careerpackage.model.TemplateDetailResponseDto;

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
        name = "Templates (Admin)",
        description = "Manage career package templates by department & position"
)
@Validated
@RequestMapping("/api/admin/templates")
public interface TemplateControllerApi {

    @Operation(
            summary = "List templates",
            description = "Returns paginated templates filtered by department and/or position"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates fetched successfully")
    })
    @GetMapping
    ResponseEntity<Page<TemplateResponseDto>> listTemplates(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );


    @Operation(
            summary = "Get template by id",
            description = "Returns full template details, including sections and required tags."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<TemplateDetailResponseDto> getTemplateById(@PathVariable Long id);

    @Operation(
            summary = "Create template",
            description = "Creates a new template for a given department and position."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Template already exists for department/position")
    })
    @PostMapping
    ResponseEntity<TemplateDetailResponseDto> createTemplate(
            @Valid @RequestBody TemplateRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Update template",
            description = "Updates department and/or position for an existing template."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    @PutMapping("/{id}")
    ResponseEntity<TemplateDetailResponseDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateUpdateRequestDto request
    );

    @Operation(
            summary = "Delete template",
            description = "Deletes a template"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTemplate(@PathVariable Long id);
}
