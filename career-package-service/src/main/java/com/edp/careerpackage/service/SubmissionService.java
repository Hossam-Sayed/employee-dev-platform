package com.edp.careerpackage.service;

import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.ManagedSubmissionResponseDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionSnapshotResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;

import java.util.List;

public interface SubmissionService {

    SubmissionResponseDto submitCareerPackage();

    List<SubmissionResponseDto> getSubmissionHistory();

    List<ManagedSubmissionResponseDto> getSubmissionsByUserIds();

    SubmissionResponseDto approveSubmission(Long submissionId, CommentRequestDto request);

    SubmissionResponseDto rejectSubmission(Long submissionId, CommentRequestDto request);

    SubmissionSnapshotResponseDto getSubmissionDetails(Long submissionId);

}
