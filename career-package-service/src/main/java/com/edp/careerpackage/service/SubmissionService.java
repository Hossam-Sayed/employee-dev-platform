package com.edp.careerpackage.service;

import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;

import java.util.List;

public interface SubmissionService {

    SubmissionResponseDto submitCareerPackage();

    List<SubmissionResponseDto> getSubmissionHistory();

    List<SubmissionTagSnapshotResponseDto> getSnapshotsBySubmissionId(Long submissionId);

}
