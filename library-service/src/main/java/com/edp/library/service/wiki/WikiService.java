package com.edp.library.service.wiki;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.wiki.WikiCreateRequestDTO;
import com.edp.library.model.wiki.WikiResponseDTO;
import com.edp.library.model.wiki.WikiSubmissionResponseDTO;

import java.util.List;

public interface WikiService {

    /**
     * User Story: As a User, I want to submit a new wiki so that it can be reviewed and published.
     * <p>
     * Creates a new wiki entry with its initial submission in PENDING status.
     * Validates that all provided tags are active.
     * A new Wiki entity is created, and the first WikiSubmission is linked as its current submission.
     *
     * @param request    The DTO containing the wiki's title, description, document URL, and tag IDs.
     * @param authorId   The ID of the author creating this wiki.
     * @param reviewerId The ID of the manager/reviewer assigned to this submission.
     * @return A {@link WikiResponseDTO} representing the newly created wiki and its current submission.
     * @throws com.edp.library.exception.ResourceNotFoundException      if any provided tag ID does not exist.
     * @throws com.edp.library.exception.InvalidOperationException      if any provided tag is not active.
     * @throws com.edp.library.exception.ResourceAlreadyExistsException if a wiki submission with the same title and document URL already exists for this author.
     */
    WikiResponseDTO createWiki(WikiCreateRequestDTO request, Long authorId, Long reviewerId);

    /**
     * User Story: As a User, I want to edit and resubmit a rejected wiki so that it can be reviewed again.
     * <p>
     * Creates a new submission for an existing wiki that currently has a REJECTED status.
     * The previous rejected submission remains in history, and the new submission becomes the current one (PENDING).
     * Validates that the requesting user is the author and that all provided tags are active.
     *
     * @param wikiId     The ID of the wiki to be resubmitted.
     * @param request    The DTO containing the updated wiki details (title, description, document URL, tag IDs).
     * @param authorId   The ID of the author attempting to edit the wiki.
     * @param reviewerId The ID of the manager/reviewer assigned to this new submission.
     * @return A {@link WikiResponseDTO} representing the wiki with its new current (PENDING) submission.
     * @throws com.edp.library.exception.ResourceNotFoundException if the wiki or any provided tag is not found.
     * @throws com.edp.library.exception.InvalidOperationException if the wiki's current submission is not REJECTED, or if the authorId does not match.
     */
    WikiResponseDTO editRejectedWikiSubmission(Long wikiId, WikiCreateRequestDTO request, Long authorId, Long reviewerId);

    /**
     * User Story: As a User, I want to see a list of my submitted wikis.
     * User Story: As a User, I want to filter my wikis by status and tags.
     * <p>
     * Retrieves a paginated list of wikis owned by a specific author.
     * Can be filtered by the status of the current submission (e.g., APPROVED, PENDING, REJECTED)
     * and/or by a specific tag ID associated with the current submission.
     *
     * @param authorId             The ID of the author whose wikis are to be retrieved.
     * @param statusFilter         Optional filter for the current submission's status.
     * @param tagIdFilter          Optional filter for a specific tag ID associated with the current submission.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link WikiResponseDTO}s.
     * @throws IllegalArgumentException if an invalid statusFilter value is provided.
     */
    PaginationResponseDTO<WikiResponseDTO> getMyWikis(Long authorId, String statusFilter, Long tagIdFilter, PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a User, I want to view the details of a specific wiki.
     * <p>
     * Retrieves the complete details of a single wiki, including its current submission and associated tags.
     *
     * @param wikiId The ID of the wiki to retrieve.
     * @return A {@link WikiResponseDTO} containing the wiki's details.
     * @throws com.edp.library.exception.ResourceNotFoundException if the wiki with the given ID is not found.
     */
    WikiResponseDTO getWikiDetails(Long wikiId);

    /**
     * User Story: As a User, I want to see the submission history of a wiki.
     * <p>
     * TODO: Check order enforcement, is it always from recent to oldest? Or controllable?
     * Retrieves a paginated list of all submissions (past and current) for a specific wiki,
     * ordered from most recent to oldest.
     *
     * @param wikiId               The ID of the wiki whose submission history is to be retrieved.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link WikiSubmissionResponseDTO}s.
     * @throws com.edp.library.exception.ResourceNotFoundException if the wiki with the given ID is not found.
     */
    PaginationResponseDTO<WikiSubmissionResponseDTO> getWikiSubmissionHistory(Long wikiId, PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a Manager, I want to see all wikis pending my review.
     * <p>
     * Retrieves a paginated list of wiki submissions that are in PENDING status and assigned to a specific reviewer.
     *
     * @param reviewerId           The ID of the manager/reviewer.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link WikiSubmissionResponseDTO}s.
     */
    PaginationResponseDTO<WikiSubmissionResponseDTO> getPendingWikiSubmissionsForReview(Long reviewerId, PaginationRequestDTO paginationRequestDTO);

    /**
     * User Story: As a Manager, I want to approve or reject a wiki submission.
     * <p>
     * Reviews a specific wiki submission, changing its status to APPROVED or REJECTED.
     * Requires the reviewer to be assigned to the submission and the submission to be in PENDING status.
     * A reviewer comment is mandatory for rejection and must meet a minimum length.
     * If approved, the associated Wiki entity's current submission is updated.
     *
     * @param submissionId The ID of the wiki submission to review.
     * @param reviewDTO    The DTO containing the new status (APPROVED/REJECTED) and an optional reviewer comment.
     * @param reviewerId   The ID of the manager performing the review (for authorization check).
     * @return A {@link WikiSubmissionResponseDTO} representing the updated submission.
     * @throws com.edp.library.exception.ResourceNotFoundException if the submission is not found.
     * @throws com.edp.library.exception.InvalidOperationException if the reviewer is not authorized,
     *                                                             if the submission is not PENDING,
     *                                                             or if a rejection comment is missing/too short.
     */
    WikiSubmissionResponseDTO reviewWikiSubmission(Long submissionId, SubmissionReviewRequestDTO reviewDTO, Long reviewerId);

    /**
     * User Story: As a User, I want to search for approved wikis by title or description.
     * User Story: As a User, I want to filter approved wikis by tags.
     * <p>
     * Retrieves a paginated list of all APPROVED and ACTIVE wikis.
     * Can be filtered by title/description (case-insensitive partial match) and/or by tag IDs.
     *
     * @param searchKeyword        Optional keyword to search in title or description.
     * @param tagIds               Optional list of tag IDs to filter by. Wikis must have at least one of these tags.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A {@link PaginationResponseDTO} containing a paginated list of {@link WikiResponseDTO}s.
     */
    PaginationResponseDTO<WikiResponseDTO> getAllApprovedAndActiveWikis(String searchKeyword, List<Long> tagIds, PaginationRequestDTO paginationRequestDTO);
}