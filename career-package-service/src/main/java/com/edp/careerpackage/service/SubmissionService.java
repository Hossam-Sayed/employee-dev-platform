package com.edp.careerpackage.service;

import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;

import java.util.List;

public interface SubmissionService {

    SubmissionResponseDto submitCareerPackage();

    List<SubmissionResponseDto> getSubmissionHistory();

    List<SubmissionTagSnapshotResponseDto> getSnapshotsBySubmissionId(Long submissionId);

    List<SubmissionResponseDto> getSubmissionsByUserIds();

    SubmissionResponseDto approveSubmission(Long submissionId, CommentRequestDto request);

    SubmissionResponseDto rejectSubmission(Long submissionId, CommentRequestDto request);
}
