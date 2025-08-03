package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import com.edp.careerpackage.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubmissionController implements SubmissionControllerApi {

    private final SubmissionService submissionService;

    @Override
    public ResponseEntity<SubmissionResponseDto> submitCareerPackage() {
        SubmissionResponseDto submission = submissionService.submitCareerPackage();
        return ResponseEntity.status(201).body(submission);
    }

    @Override
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissionHistory() {
        List<SubmissionResponseDto> history = submissionService.getSubmissionHistory();
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<List<SubmissionTagSnapshotResponseDto>> getSubmissionSnapshots(Long submissionId) {
        return ResponseEntity.ok(submissionService.getSnapshotsBySubmissionId(submissionId));
    }

    @Override
    public ResponseEntity<List<SubmissionResponseDto>> searchSubmissions(@RequestParam List<Long> userIds) {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserIds(userIds));
    }

    @Override
    public ResponseEntity<SubmissionResponseDto> approveSubmission(Long submissionId, @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(submissionService.approveSubmission(submissionId, request));
    }

    @Override
    public ResponseEntity<SubmissionResponseDto> rejectSubmission(Long submissionId, @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(submissionService.rejectSubmission(submissionId, request));
    }
}
