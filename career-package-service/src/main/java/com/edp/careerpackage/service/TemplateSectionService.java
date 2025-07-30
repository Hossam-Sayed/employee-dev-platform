package com.edp.careerpackage.service;

import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

import java.util.List;

public interface TemplateSectionService {

    TemplateSectionResponseDto addSection(Long templateId, TemplateSectionRequestDto request);

    void removeSection(Long templateSectionId);

    List<TemplateSectionRequiredTagResponseDto> listRequiredTags(Long templateSectionId);
}
