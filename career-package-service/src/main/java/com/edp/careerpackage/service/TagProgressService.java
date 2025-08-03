package com.edp.careerpackage.service;

import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;

public interface TagProgressService {

    TagPogressResponseDto updateTagProgress(Long tagProgressId, TagProgressRequestDto request);
}
