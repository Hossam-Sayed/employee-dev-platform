package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.service.TemplateSectionRequiredTagService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class TemplateSectionRequiredTagController implements TemplateSectionRequiredTagControllerApi {

    private final TemplateSectionRequiredTagService requiredTagService;

    @Override
    public ResponseEntity<TemplateSectionRequiredTagResponseDto> attachRequiredTag(
            TemplateSectionRequiredTagRequestDto request, UriComponentsBuilder uriBuilder
    ) {
        TemplateSectionRequiredTagResponseDto created =
                requiredTagService.addRequiredTag(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/section-required-tags/{id}")
                        .buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @Override
    public ResponseEntity<Void> detachRequiredTag(Long requiredTagId) {
        requiredTagService.removeRequiredTag(requiredTagId);
        return ResponseEntity.noContent().build();
    }
}
