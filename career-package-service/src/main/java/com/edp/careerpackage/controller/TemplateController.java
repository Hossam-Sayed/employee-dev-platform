package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import com.edp.careerpackage.service.TemplateService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplateControllerApi {

    private final TemplateService templateService;

    @Override
    public ResponseEntity<Page<TemplateResponseDto>> listTemplates(
            String department,
            String position,
            int page,
            int size
    ) {
        Page<TemplateResponseDto> templates = templateService.listTemplates(department, position, page, size);
        return ResponseEntity.ok(templates);
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> getTemplateById(Long id) {
        TemplateDetailResponseDto template = templateService.getTemplateById(id);
        return ResponseEntity.ok(template);
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> createTemplate(
            TemplateRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        TemplateDetailResponseDto created = templateService.createTemplate(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/admin/templates/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<TemplateDetailResponseDto> updateTemplate(Long id, TemplateUpdateRequestDto request) {
        TemplateDetailResponseDto updated = templateService.updateTemplate(id, request);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
