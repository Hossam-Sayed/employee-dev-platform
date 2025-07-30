package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.service.TemplateSectionRequiredTagService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TemplateSectionRequiredTagController implements TemplateSectionRequiredTagControllerApi {

    private final TemplateSectionRequiredTagService requiredTagService;

    @Override
    public ResponseEntity<List<TemplateSectionRequiredTagResponseDto>> listRequiredTags(Long templateId, Long templateSectionId) {
        return ResponseEntity.ok(requiredTagService.listRequiredTags(templateId, templateSectionId));
    }

    @Override
    public ResponseEntity<TemplateSectionRequiredTagResponseDto> addRequiredTag(Long templateId, Long templateSectionId,
                                                                                TemplateSectionRequiredTagRequestDto request,
                                                                                UriComponentsBuilder uriBuilder) {
        TemplateSectionRequiredTagResponseDto created = requiredTagService.addRequiredTag(templateId, templateSectionId, request);
        return ResponseEntity
                .created(uriBuilder.path("/api/admin/templates/{templateId}/sections/{templateSectionId}/required-tags/{id}")
                        .buildAndExpand(templateId, templateSectionId, created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<Void> removeRequiredTag(Long templateId, Long templateSectionId, Long requiredTagId) {
        requiredTagService.removeRequiredTag(templateId, templateSectionId, requiredTagId);
        return ResponseEntity.noContent().build();
    }
}
