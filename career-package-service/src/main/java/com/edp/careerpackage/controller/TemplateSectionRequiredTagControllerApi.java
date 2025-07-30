package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
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
        name = "Template Section Required Tags (Admin)",
        description = "Attach, remove or list required tags & criteria for a template section"
)
@Validated
@RequestMapping("/api/admin/templates/{templateId}/sections/{templateSectionId}/required-tags")
public interface TemplateSectionRequiredTagControllerApi {

    @Operation(
            summary = "List required tags in template section",
            description = "Returns all required tags and criteria for the given template section."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Required tags fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Template or section not found")
    })
    @GetMapping
    ResponseEntity<List<TemplateSectionRequiredTagResponseDto>> listRequiredTags(
            @PathVariable Long templateId,
            @PathVariable Long templateSectionId
    );

    @Operation(
            summary = "Attach required tag to template section",
            description = "Attaches a tag with criteria type & min value to the template section."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Required tag attached successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Template, section, or tag not found"),
            @ApiResponse(responseCode = "409", description = "Tag already attached to this section")
    })
    @PostMapping
    ResponseEntity<TemplateSectionRequiredTagResponseDto> addRequiredTag(
            @PathVariable Long templateId,
            @PathVariable Long templateSectionId,
            @Valid @RequestBody TemplateSectionRequiredTagRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Detach required tag from template section",
            description = "Removes a required tag from the template section."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Required tag detached successfully"),
            @ApiResponse(responseCode = "404", description = "Required tag not found")
    })
    @DeleteMapping("/{requiredTagId}")
    ResponseEntity<Void> removeRequiredTag(
            @PathVariable Long templateId,
            @PathVariable Long templateSectionId,
            @PathVariable Long requiredTagId
    );
}
