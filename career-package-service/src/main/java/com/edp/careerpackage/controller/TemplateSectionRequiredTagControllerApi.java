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

@Tag(
        name = "Template Section Required Tags",
        description = "Attach or detach required tags from template sections"
)
@Validated
@RequestMapping("/api/section-required-tags")
public interface TemplateSectionRequiredTagControllerApi {

    @Operation(
            summary = "Attach required tag",
            description = "Attach a required tag to a template section"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Required tag attached successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "TemplateSection or Tag not found"),
            @ApiResponse(responseCode = "409", description = "Tag already attached")
    })
    @PostMapping
    ResponseEntity<TemplateSectionRequiredTagResponseDto> attachRequiredTag(
            @Valid @RequestBody TemplateSectionRequiredTagRequestDto request,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Detach required tag",
            description = "Detach a required tag from a template section"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Required tag detached successfully"),
            @ApiResponse(responseCode = "404", description = "RequiredTag not found")
    })
    @DeleteMapping("/{requiredTagId}")
    ResponseEntity<Void> detachRequiredTag(@PathVariable Long requiredTagId);
}
