package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
        name = "Career Package Section Progress",
        description = "Manage career package section progress and its tags progress"
)
@Validated
@RequestMapping("/api/career-package-sections")
public interface CareerPackageSectionProgressControllerApi {

    @Operation(
            summary = "Get a single career package section progress",
            description = "Returns all details for a specific section progress"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Section progress fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Section progress not found")
    })
    @GetMapping("/{sectionProgressId}")
    ResponseEntity<SectionProgressResponseDto> getSectionProgress(
            @PathVariable Long sectionProgressId
    );

    @Operation(
            summary = "List tags progress for a section progress",
            description = "Returns all tags progress attached to a given section progress"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Required tags progress fetched successfully"),
            @ApiResponse(responseCode = "404", description = "section progress not found")
    })
    @GetMapping("/{sectionProgressId}/tags-progress")
    ResponseEntity<List<TagPogressResponseDto>> listTagsProgress(
            @PathVariable Long sectionProgressId
    );
}