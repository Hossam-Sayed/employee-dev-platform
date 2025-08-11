package com.edp.careerpackage.service;

import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;

import java.util.List;

public interface CareerPackageSectionProgressService {

    SectionProgressResponseDto getSectionProgress(Long sectionProgressId);

    List<TagPogressResponseDto> listTagsProgress (Long sectionProgressId);
}