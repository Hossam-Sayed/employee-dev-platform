package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;
import com.edp.careerpackage.model.tagprogress.TagProgressResponseDto;
import com.edp.careerpackage.service.TagProgressService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagProgressController implements TagProgressControllerApi {

    private final TagProgressService tagProgressService;

    @Override
    public ResponseEntity<TagProgressResponseDto> updateTagProgress(
            Long tagProgressId,
            TagProgressRequestDto request
    ) {
        TagProgressResponseDto updated = tagProgressService.updateTagProgress(tagProgressId, request);
        return ResponseEntity.ok(updated);
    }
}
