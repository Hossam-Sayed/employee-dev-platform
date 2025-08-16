package com.edp.library.controller.learning;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.learning.ApprovedLearningByEmployeeResponseDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;
import com.edp.library.service.learning.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LearningControllerImpl implements LearningController {

    private final LearningService learningService;

    @Override
    public ResponseEntity<LearningResponseDTO> createLearning(
            LearningCreateRequestDTO request
    ) {
        LearningResponseDTO response = learningService.createLearning(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<LearningResponseDTO> editRejectedLearningSubmission(
            Long learningId,
            LearningCreateRequestDTO request
    ) {
        LearningResponseDTO response = learningService.editRejectedLearningSubmission(learningId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<LearningResponseDTO>> getMyLearnings(
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<LearningResponseDTO> response =
                learningService.getMyLearnings(statusFilter, tagIdFilter, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LearningResponseDTO> getLearningDetails(Long learningId) {
        LearningResponseDTO response = learningService.getLearningDetails(learningId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<LearningSubmissionResponseDTO>> getLearningSubmissionHistory(
            Long learningId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<LearningSubmissionResponseDTO> response =
                learningService.getLearningSubmissionHistory(learningId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<LearningSubmissionResponseDTO>> getPendingLearningSubmissionsForReview(
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<LearningSubmissionResponseDTO> response =
                learningService.getPendingLearningSubmissionsForReview(paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LearningSubmissionResponseDTO> reviewLearningSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO
    ) {
        LearningSubmissionResponseDTO response =
                learningService.reviewLearningSubmission(submissionId, reviewDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<ApprovedLearningByEmployeeResponseDTO>> getApprovedLearningsByEmployee(PaginationRequestDTO paginationRequestDTO) {
        PaginationResponseDTO<ApprovedLearningByEmployeeResponseDTO> response = learningService.getApprovedLearningsByEmployee(paginationRequestDTO);
        return ResponseEntity.ok(response);
    }
}