package com.edp.library.controller.tag;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.tag.TagCreateRequestDTO;
import com.edp.library.model.tag.TagDTO;
import com.edp.library.model.tag.TagRequestResponseDTO;
import com.edp.library.model.tag.TagRequestReviewDTO;
import com.edp.library.model.tag.TagUpdateStatusDTO;
import com.edp.library.service.tag.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    // Assuming a user can submit a tag request
    @Override
    public ResponseEntity<TagRequestResponseDTO> createTagRequest(
            TagCreateRequestDTO request,
            Long requesterId
    ) {
        TagRequestResponseDTO response = tagService.createTagRequest(request, requesterId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint for users/general public to get approved and active tags
    @Override
    public ResponseEntity<List<TagDTO>> getAllApprovedAndActiveTags(String nameFilter) {
        List<TagDTO> tags = tagService.getAllApprovedAndActiveTags(nameFilter);
        return ResponseEntity.ok(tags);
    }

    // Endpoint for a specific user to view their own tag requests
    @Override
    public ResponseEntity<PaginationResponseDTO<TagRequestResponseDTO>> getMyTagRequests(
            Long requesterId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<TagRequestResponseDTO> response =
                tagService.getMyTagRequests(requesterId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Endpoint for admins to view all pending tag requests
    @Override
    public ResponseEntity<PaginationResponseDTO<TagRequestResponseDTO>> getAllPendingTagRequests(
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<TagRequestResponseDTO> response =
                tagService.getAllPendingTagRequests(paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Endpoint for admins to review a specific tag request
    @Override
    public ResponseEntity<TagRequestResponseDTO> reviewTagRequest(
            Long tagRequestId,
            TagRequestReviewDTO reviewDTO,
            Long reviewerId
    ) {
        TagRequestResponseDTO response = tagService.reviewTagRequest(tagRequestId, reviewDTO, reviewerId);
        return ResponseEntity.ok(response);
    }

    // Endpoint for admins to get all tags (including inactive ones)
    @Override
    public ResponseEntity<PaginationResponseDTO<TagDTO>> getAllTagsForAdmin(
            String nameFilter,
            Boolean isActiveFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<TagDTO> response =
                tagService.getAllTagsForAdmin(nameFilter, isActiveFilter, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Endpoint for admins to update a tag's active status
    @Override
    public ResponseEntity<TagDTO> updateTagStatus(
            Long tagId,
            TagUpdateStatusDTO updateStatusDTO,
            Long adminId
    ) {
        TagDTO response = tagService.updateTagStatus(tagId, updateStatusDTO, adminId);
        return ResponseEntity.ok(response);
    }

    // Endpoint for admins to directly create a tag
    @Override
    public ResponseEntity<TagDTO> createTagByAdmin(
            TagCreateRequestDTO tagCreateRequestDTO,
            Long adminId
    ) {
        TagDTO response = tagService.createTagByAdmin(tagCreateRequestDTO, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}