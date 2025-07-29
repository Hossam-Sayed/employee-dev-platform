package com.edp.careerpackage.service;

import com.edp.careerpackage.model.SectionRequestDto;
import com.edp.careerpackage.model.SectionResponseDto;

import java.util.List;

public interface SectionService {

    List<SectionResponseDto> searchSections(String query);

    SectionResponseDto createSection(SectionRequestDto request);
}
