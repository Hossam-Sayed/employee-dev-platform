package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
