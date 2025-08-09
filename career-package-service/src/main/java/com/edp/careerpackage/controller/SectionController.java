package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.section.SectionRequestDto;
import com.edp.careerpackage.model.section.SectionResponseDto;
import com.edp.careerpackage.service.SectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
public class SectionController implements SectionControllerApi {

    private final SectionService sectionService;

    @Override
    public ResponseEntity<List<SectionResponseDto>> searchSections(String query) {
        return ResponseEntity.ok(sectionService.searchSections(query));
    }

    @Override
    public ResponseEntity<SectionResponseDto> createSection(SectionRequestDto request, UriComponentsBuilder uriBuilder) {
        SectionResponseDto created = sectionService.createSection(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/sections/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }
}
