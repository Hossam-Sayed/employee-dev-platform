package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import com.edp.careerpackage.service.TemplateService;
import com.edp.careerpackage.service.TemplateSectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplateControllerApi {

    private final TemplateService templateService;
    private final TemplateSectionService templateSectionService;

    @Override
    public ResponseEntity<Page<TemplateResponseDto>> listTemplates(
            String department, String position, int page, int size
    ) {
        return ResponseEntity.ok(templateService.listTemplates(department, position, page, size));
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> getTemplateById(Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> createTemplate(
            TemplateRequestDto request, UriComponentsBuilder uriBuilder
    ) {
        TemplateDetailResponseDto created = templateService.createTemplate(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/templates/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> updateTemplate(Long id, TemplateUpdateRequestDto request) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TemplateSectionResponseDto>> listSections(Long templateId) {
        return ResponseEntity.ok(templateService.listSections(templateId));
    }

}
