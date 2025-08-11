package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.careerpackage.service.CareerPackageSectionProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
public class CareerPackageSectionProgressController implements CareerPackageSectionProgressControllerApi {

    private final CareerPackageSectionProgressService careerPackageSectionProgressService;

    @Override
    public ResponseEntity<SectionProgressResponseDto> getSectionProgress(Long sectionProgressId) {
        return ResponseEntity.ok(careerPackageSectionProgressService.getSectionProgress(sectionProgressId));
    }

    @Override
    public ResponseEntity<List<TagPogressResponseDto>> listTagsProgress(Long sectionProgressId) {
        return ResponseEntity.ok(careerPackageSectionProgressService.listTagsProgress(sectionProgressId));
    }


}