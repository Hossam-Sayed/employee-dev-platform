package com.edp.careerpackage.service;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;

import java.util.List;

public interface TemplateSectionService {

    List<TemplateSectionResponseDto> listSections(Long templateId);

    TemplateSectionResponseDto addSection(Long templateId, TemplateSectionRequestDto request);

    void removeSection(Long templateId, Long templateSectionId);
}
