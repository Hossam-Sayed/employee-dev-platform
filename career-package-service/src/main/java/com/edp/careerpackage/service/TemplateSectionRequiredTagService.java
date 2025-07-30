package com.edp.careerpackage.service;

import com.edp.careerpackage.model.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.TemplateSectionRequiredTagResponseDto;

import java.util.List;

public interface TemplateSectionRequiredTagService {

    List<TemplateSectionRequiredTagResponseDto> listRequiredTags(Long templateId, Long templateSectionId);

    TemplateSectionRequiredTagResponseDto addRequiredTag(Long templateId, Long templateSectionId,
                                                         TemplateSectionRequiredTagRequestDto request);

    void removeRequiredTag(Long templateId, Long templateSectionId, Long requiredTagId);
}
