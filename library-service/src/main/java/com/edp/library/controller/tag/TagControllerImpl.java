package com.edp.library.controller.tag;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.tag.TagCreateRequestDTO;
import com.edp.library.model.tag.TagRequestResponseDTO;
import com.edp.library.model.tag.TagRequestReviewDTO;
import com.edp.library.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    // Endpoint for users to submit a tag request
    @Override
    public ResponseEntity<TagRequestResponseDTO> createTagRequest(
            TagCreateRequestDTO request
    ) {
        TagRequestResponseDTO response = tagService.createTagRequest(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint for a specific user to view their own tag requests
    @Override
    public ResponseEntity<PaginationResponseDTO<TagRequestResponseDTO>> getMyTagRequests(
            Long requesterId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<TagRequestResponseDTO> response =
                tagService.getMyTagRequests(paginationRequestDTO);
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
        TagRequestResponseDTO response = tagService.reviewTagRequest(tagRequestId, reviewDTO);
        return ResponseEntity.ok(response);
    }
}