package com.edp.library.service.tag;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.tag.*;

public interface TagService {

    /**
     * Creates a new tag request.
     * User Story: As a USER, I need to request a new tag.
     *
     * @param request The DTO containing the requested tag name.
     * @return The created TagRequestResponseDTO.
     */
    TagRequestResponseDTO createTagRequest(TagCreateRequestDTO request);

    /**
     * Prevents duplicate tag requests by checking if a tag with the same name (case-insensitive)
     * is already exists.
     * User Story: As a USER, I should be prevented from submitting a duplicate tag request if one with the same name is already exists.
     *
     * @param tagName The name of the tag to check.
     * @return true if a duplicate exists, false otherwise.
     */
    boolean isDuplicateTagRequestOrApprovedTag(String tagName);

    /**
     * Retrieves a paginated list of tag requests for a specific user.
     * User Story: As a USER, I need to view my tag requests with their current status.
     *
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of TagRequestResponseDTOs.
     */
    PaginationResponseDTO<TagRequestResponseDTO> getMyTagRequests(PaginationRequestDTO paginationRequestDTO);

    /**
     * Retrieves a paginated list of all pending tag requests for admin review.
     * User Story: As an ADMIN, I need to view all pending tag requests.
     * User Story: As an ADMIN, I need to view the details of a pending tag request.
     *
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of TagRequestResponseDTOs.
     */
    PaginationResponseDTO<TagRequestResponseDTO> getAllPendingTagRequests(PaginationRequestDTO paginationRequestDTO);

    /**
     * Admin reviews a pending tag request (approves or rejects).
     * User Story: As an ADMIN, I need to approve/Reject w/ comment any pending tag request.
     * User Story: As an ADMIN, when I approve a tag request, the system should automatically create a new entry in the main 'Tag' table.
     * User Story: As an ADMIN, I need to be prompted to provide a comment of at least X characters when rejecting a tag request.
     *
     * @param tagRequestId The ID of the tag request to review.
     * @param reviewDTO    The DTO containing the review status and comment.
     * @return The updated TagRequestResponseDTO.
     */
    TagRequestResponseDTO reviewTagRequest(Long tagRequestId, TagRequestReviewDTO reviewDTO);
}