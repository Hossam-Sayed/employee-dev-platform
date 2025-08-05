package com.edp.library.service.tag;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.tag.*;

import java.util.List;

public interface TagService {

    /**
     * Creates a new tag request.
     * User Story: As a USER, I need to request a new tag.
     *
     * @param request     The DTO containing the requested tag name.
     * @param requesterId The ID of the user requesting the tag.
     * @return The created TagRequestResponseDTO.
     */
    TagRequestResponseDTO createTagRequest(TagCreateRequestDTO request, Long requesterId);

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
     * Retrieves all approved and active tags.
     * User Story: As a USER, I need to view all approved tags.
     * User Story: As a USER, I need to search for approved tags to quickly find relevant ones. (Filtering will be in impl)
     *
     * @param nameFilter Optional filter for tag name (case-insensitive contains).
     * @return A list of TagDTOs.
     */
    List<TagDTO> getAllApprovedAndActiveTags(String nameFilter);

    /**
     * Retrieves a paginated list of tag requests for a specific user.
     * User Story: As a USER, I need to view my tag requests with their current status.
     *
     * @param requesterId          The ID of the user whose tag requests are to be retrieved.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of TagRequestResponseDTOs.
     */
    PaginationResponseDTO<TagRequestResponseDTO> getMyTagRequests(Long requesterId, PaginationRequestDTO paginationRequestDTO);

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
     * @param reviewerId   The ID of the admin reviewing the request.
     * @return The updated TagRequestResponseDTO.
     */
    TagRequestResponseDTO reviewTagRequest(Long tagRequestId, TagRequestReviewDTO reviewDTO, Long reviewerId);

    /**
     * Retrieves a paginated list of all tags (including inactive) for admin management.
     * User Story: As an ADMIN, I need to view all tags in the system with their status.
     * User Story: As an ADMIN, I need to search, filter, and paginate the list of all tags for easier management.
     *
     * @param nameFilter           Optional filter for tag name.
     * @param isActiveFilter       Optional filter for active status.
     * @param paginationRequestDTO Pagination and sorting parameters.
     * @return A paginated response of TagDTOs.
     */
    PaginationResponseDTO<TagDTO> getAllTagsForAdmin(String nameFilter, Boolean isActiveFilter, PaginationRequestDTO paginationRequestDTO);

    /**
     * Admin deactivates or activates an existing tag.
     * User Story: As an ADMIN, I need to be able to deactivate/archive a tag so it can no longer be used for new materials.
     *
     * @param tagId           The ID of the tag to update.
     * @param updateStatusDTO The DTO containing the new active status.
     * @param adminId         The ID of the admin performing the action.
     * @return The updated TagDTO.
     */
    TagDTO updateTagStatus(Long tagId, TagUpdateStatusDTO updateStatusDTO, Long adminId);

    /**
     * User Story: As an Admin, I need to be able to add new tags directly to the system
     * so that employees can categorize their learning materials, wikis, and blogs effectively.
     * <p>
     * Creates a new tag in the system directly by an administrator.
     * Before creating, it checks if a tag with the same name (case-insensitive) already exists.
     *
     * @param tagCreateRequestDTO The DTO containing the requested name for the new tag.
     * @param adminId             The ID of the administrator performing this action, used for auditing.
     * @return A {@link TagDTO} representing the newly created tag.
     * @throws com.edp.library.exception.ResourceAlreadyExistsException if a tag with the same name already exists.
     * @throws IllegalArgumentException                                 if the tag name is null or empty.
     */
    TagDTO createTagByAdmin(TagCreateRequestDTO tagCreateRequestDTO, Long adminId);
}