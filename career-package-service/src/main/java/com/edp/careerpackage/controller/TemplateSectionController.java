package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.service.TemplateSectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TemplateSectionController implements TemplateSectionControllerApi {

    private final TemplateSectionService templateSectionService;

    @Override
    public ResponseEntity<TemplateSectionResponseDto> attachSection(
            TemplateSectionRequestDto request, UriComponentsBuilder uriBuilder
    ) {
        TemplateSectionResponseDto created = templateSectionService.addSection(request.getTemplateId(), request);
        return ResponseEntity
                .created(uriBuilder.path("/api/template-sections/{id}")
                        .buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<Void> detachSection(Long templateSectionId) {
        templateSectionService.removeSection(templateSectionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TemplateSectionRequiredTagResponseDto>> listRequiredTags(Long templateSectionId) {
        return ResponseEntity.ok(templateSectionService.listRequiredTags(templateSectionId));
    }
}
