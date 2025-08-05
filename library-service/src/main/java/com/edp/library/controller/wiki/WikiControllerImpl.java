package com.edp.library.controller.wiki;

import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.SubmissionReviewRequestDTO;
import com.edp.library.model.wiki.WikiCreateRequestDTO;
import com.edp.library.model.wiki.WikiResponseDTO;
import com.edp.library.model.wiki.WikiSubmissionResponseDTO;
import com.edp.library.service.wiki.WikiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WikiControllerImpl implements WikiController {

    private final WikiService wikiService;

    // TODO: Determine reviewerId internally by business logic, not client provided

    // TODO: For reviewers Endpoints
    //  Authentication system would ideally ensure the current user is a reviewer/manager
    //  and map their ID to 'reviewerId' or validate that 'X-Reviewer-Id' matches their ID.

    @Override
    public ResponseEntity<WikiResponseDTO> createWiki(
            WikiCreateRequestDTO request,
            Long authorId,
            Long reviewerId
    ) {
        WikiResponseDTO response = wikiService.createWiki(request, authorId, reviewerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WikiResponseDTO> editRejectedWikiSubmission(
            Long wikiId,
            WikiCreateRequestDTO request,
            Long authorId,
            Long reviewerId
    ) {
        WikiResponseDTO response = wikiService.editRejectedWikiSubmission(wikiId, request, authorId, reviewerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<WikiResponseDTO>> getMyWikis(
            Long authorId,
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiResponseDTO> response = wikiService.getMyWikis(authorId, statusFilter, tagIdFilter, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WikiResponseDTO> getWikiDetails(Long wikiId) {
        WikiResponseDTO response = wikiService.getWikiDetails(wikiId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<WikiSubmissionResponseDTO>> getWikiSubmissionHistory(
            Long wikiId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiSubmissionResponseDTO> response = wikiService.getWikiSubmissionHistory(wikiId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<WikiSubmissionResponseDTO>> getPendingWikiSubmissionsForReview(
            Long reviewerId,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiSubmissionResponseDTO> response = wikiService.getPendingWikiSubmissionsForReview(reviewerId, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WikiSubmissionResponseDTO> reviewWikiSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO,
            Long reviewerId
    ) {
        WikiSubmissionResponseDTO response = wikiService.reviewWikiSubmission(submissionId, reviewDTO, reviewerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<WikiResponseDTO>> getAllApprovedAndActiveWikis(
            String searchKeyword,
            List<Long> tagIds,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiResponseDTO> response = wikiService.getAllApprovedAndActiveWikis(searchKeyword, tagIds, paginationRequestDTO);
        return ResponseEntity.ok(response);
    }
}