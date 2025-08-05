package com.edp.library.service.learning;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.learning.LearningCreateRequestDTO;
import com.edp.library.model.learning.LearningResponseDTO;
import com.edp.library.model.learning.LearningSubmissionResponseDTO;

public interface LearningService {

    /**
     * Creates a new learning material and its initial submission.
     * User Story: As a USER, I need to create a new material (learning).
     * User Story: As a USER, I need to add existing approved tags to my new material submission.
     *
     * @param request     The DTO containing learning material details.
     * @param submitterId The ID of the user submitting the material.
     * @param reviewerId  The ID of the manager assigned to review this submission.
     * @return The created LearningResponseDTO.
     */
    LearningResponseDTO createLearning(LearningCreateRequestDTO request, Long submitterId, Long reviewerId);

    /**
     * Edits a rejected learning submission, creating a new pending submission.
     * User Story: As a USER, I need to edit a rejected submission.
     * User Story: As a USER, when editing a rejected submission, I need to pre-fill the form with data from the last rejected submission to make corrections easier. (This is client-side, but implies service provides the data)
     *
     * @param learningId  The ID of the learning material to edit.
     * @param request     The DTO containing the updated learning material details.
     * @param submitterId The ID of the user submitting the material.
     * @param reviewerId  The ID of the manager assigned to review this submission.
     * @return The updated LearningResponseDTO.
     */
    LearningResponseDTO editRejectedLearningSubmission(Long learningId, LearningCreateRequestDTO request, Long submitterId, Long reviewerId);

    /**
     * Retrieves a paginated list of learning materials owned by a specific user, with optional filters.
     * User Story: As a USER, I need to view my materials with their status.
     * User Story: As a USER, I need to view my materials with pagination and filtering options (e.g., by status, by tag, by type) to easily navigate through them.
     *
     * @param employeeId           The ID of the employee whose materials are to be retrieved.
     * @param statusFilter         Optional filter by submission status.
     * @param tagIdFilter          Optional filter by tag ID.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of LearningResponseDTOs.
     */
    PaginationResponseDTO<LearningResponseDTO> getMyLearnings(Long employeeId, String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO);

    /**
     * Retrieves the details of a single learning material.
     * User Story: As a USER, I need to view the details of a single material with its details.
     *
     * @param learningId The ID of the learning material.
     * @return The LearningResponseDTO.
     */
    LearningResponseDTO getLearningDetails(Long learningId);

    /**
     * Retrieves the full submission history for a specific learning material.
     * User Story: As a USER, I need to view the full submission history for a specific material, including past rejected versions and manager comments.
     *
     * @param learningId           The ID of the learning material.
     * @param paginationRequestDTO Pagination and sorting parameters for submissions.
     * @return A paginated response of LearningSubmissionResponseDTOs.
     */
    PaginationResponseDTO<LearningSubmissionResponseDTO> getLearningSubmissionHistory(Long learningId, PaginationRequestDTO paginationRequestDTO);

    /**
     * Retrieves a paginated list of pending learning submissions assigned to a specific manager.
     * User Story: As a MANAGER, I need to view pending submissions assigned to me.
     *
     * @param managerId            The ID of the manager.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of LearningSubmissionResponseDTOs.
     */
    PaginationResponseDTO<LearningSubmissionResponseDTO> getPendingLearningSubmissionsForReview(Long managerId, PaginationRequestDTO paginationRequestDTO);

    /**
     * Manager approves or rejects a learning submission.
     * User Story: As a MANAGER, I need to approve/reject w/ comment any pending submissions assigned to me.
     * User Story: As a MANAGER, I need to be prompted to provide a comment of at least X characters when rejecting a submission.
     *
     * @param submissionId The ID of the submission to review.
     * @param reviewDTO    The DTO containing the review status and comment.
     * @param reviewerId   The ID of the manager reviewing the submission.
     * @return The updated LearningSubmissionResponseDTO.
     */
    LearningSubmissionResponseDTO reviewLearningSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO, Long reviewerId);

    // Potentially needed for "Explore all blogs and wikis" but not directly for learning
    // PaginationResponseDTO<LearningResponseDTO> getAllApprovedLearnings(String titleFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO);
}