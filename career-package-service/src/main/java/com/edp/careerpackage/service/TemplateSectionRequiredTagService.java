package com.edp.careerpackage.service;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

public interface TemplateSectionRequiredTagService {

    TemplateSectionRequiredTagResponseDto addRequiredTag(TemplateSectionRequiredTagRequestDto request);

    void removeRequiredTag(Long requiredTagId);
}
