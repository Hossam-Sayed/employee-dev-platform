package com.edp.careerpackage.service;

import com.edp.careerpackage.model.submission.SubmissionResponseDto;

import java.util.List;

public interface SubmissionService {

    SubmissionResponseDto submitCareerPackage();

    List<SubmissionResponseDto> getSubmissionHistory();
}
