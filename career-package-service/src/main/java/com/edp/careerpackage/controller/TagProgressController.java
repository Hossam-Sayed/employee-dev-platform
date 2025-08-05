package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;
import com.edp.careerpackage.service.TagProgressService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TagProgressController implements TagProgressControllerApi {

    private final TagProgressService tagProgressService;

    @Override
    public ResponseEntity<TagPogressResponseDto> updateTagProgress(
            Long tagProgressId,
            Double completedValue,
            String proofUrl,
            MultipartFile file
    ) {
        TagPogressResponseDto updated = tagProgressService.updateTagProgress(tagProgressId, completedValue, proofUrl, file);
        return ResponseEntity.ok(updated);
    }
}
