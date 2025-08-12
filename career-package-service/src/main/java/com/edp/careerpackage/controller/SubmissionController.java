package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.ManagedSubmissionResponseDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionSnapshotResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import com.edp.careerpackage.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<List<ManagedSubmissionResponseDto>> searchSubmissions() {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserIds());
    }

    @Override
    public ResponseEntity<SubmissionResponseDto> approveSubmission(Long submissionId, @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(submissionService.approveSubmission(submissionId, request));
    }

    @Override
    public ResponseEntity<SubmissionResponseDto> rejectSubmission(Long submissionId, @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(submissionService.rejectSubmission(submissionId, request));
    }

    @Override
    public ResponseEntity<SubmissionSnapshotResponseDto> getSubmissionDetails(Long submissionId){
        return ResponseEntity.ok(submissionService.getSubmissionDetails(submissionId));
    }
}
