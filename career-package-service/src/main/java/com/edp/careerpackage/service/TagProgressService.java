package com.edp.careerpackage.service;

import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.careerpackage.model.tagprogress.TagProgressRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface TagProgressService {

    TagPogressResponseDto updateTagProgress(Long tagProgressId, Double completedValue,String proofUrl, MultipartFile file);
}
