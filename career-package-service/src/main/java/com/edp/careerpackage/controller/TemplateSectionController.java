package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
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
    public ResponseEntity<List<TemplateSectionResponseDto>> listSections(Long templateId) {
        return ResponseEntity.ok(templateSectionService.listSections(templateId));
    }

    @Override
    public ResponseEntity<TemplateSectionResponseDto> addSection(Long templateId, TemplateSectionRequestDto request, UriComponentsBuilder uriBuilder) {
        TemplateSectionResponseDto created = templateSectionService.addSection(templateId, request);
        return ResponseEntity
                .created(uriBuilder.path("/api/admin/templates/{templateId}/sections/{id}")
                        .buildAndExpand(templateId, created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<Void> removeSection(Long templateId, Long templateSectionId) {
        templateSectionService.removeSection(templateId, templateSectionId);
        return ResponseEntity.noContent().build();
    }
}
