package com.edp.library.controller.learning;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;
import com.edp.library.service.learning.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LearningControllerImpl implements LearningController {

    private final LearningService learningService;

    @Override
    public ResponseEntity<LearningResponseDTO> createLearning(
            LearningCreateRequestDTO request,
            Long submitterId,
            Long reviewerId
    ) {
        LearningResponseDTO response = learningService.createLearning(request, submitterId, reviewerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<LearningResponseDTO> editRejectedLearningSubmission(
            Long learningId,
            LearningCreateRequestDTO request,
            Long submitterId,
            Long reviewerId
    ) {
        LearningResponseDTO response = learningService.editRejectedLearningSubmission(learningId, request, submitterId, reviewerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<LearningResponseDTO>> getMyLearnings(
            Long employeeId,
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<LearningResponseDTO> response =
                learningService.getMyLearnings(employeeId, statusFilter, tagIdFilter, paginationRequestDTO);
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
            Long managerId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<LearningSubmissionResponseDTO> response =
                learningService.getPendingLearningSubmissionsForReview(managerId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LearningSubmissionResponseDTO> reviewLearningSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO,
            Long reviewerId
    ) {
        LearningSubmissionResponseDTO response =
                learningService.reviewLearningSubmission(submissionId, reviewDTO, reviewerId);
        return ResponseEntity.ok(response);
    }
}