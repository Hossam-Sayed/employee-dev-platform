package com.edp.careerpackage.service;

import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;
import com.edp.careerpackage.model.tagprogress.TagProgressResponseDto;

public interface TagProgressService {

    TagProgressResponseDto updateTagProgress(Long tagProgressId, TagProgressRequestDto request);
}
