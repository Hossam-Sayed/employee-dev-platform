package com.edp.careerpackage.service;

import com.edp.careerpackage.model.section.SectionRequestDto;
import com.edp.careerpackage.model.section.SectionResponseDto;

import java.util.List;

public interface SectionService {

    List<SectionResponseDto> searchSections(String query);

    SectionResponseDto createSection(SectionRequestDto request);
}
