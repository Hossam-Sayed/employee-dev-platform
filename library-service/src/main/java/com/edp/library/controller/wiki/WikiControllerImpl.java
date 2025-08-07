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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WikiControllerImpl implements WikiController {

    private final WikiService wikiService;

    @Override
    public ResponseEntity<WikiResponseDTO> createWiki(
            WikiCreateRequestDTO request
    ) {
        WikiResponseDTO response = wikiService.createWiki(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WikiResponseDTO> editRejectedWikiSubmission(
            Long wikiId,
            WikiCreateRequestDTO request
    ) {
        WikiResponseDTO response = wikiService.editRejectedWikiSubmission(wikiId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginationResponseDTO<WikiResponseDTO>> getMyWikis(
            String statusFilter,
            Long tagIdFilter,
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiResponseDTO> response = wikiService.getMyWikis(statusFilter, tagIdFilter, paginationRequestDTO);
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
            PaginationRequestDTO paginationRequestDTO
    ) {
        PaginationResponseDTO<WikiSubmissionResponseDTO> response = wikiService.getPendingWikiSubmissionsForReview(paginationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WikiSubmissionResponseDTO> reviewWikiSubmission(
            Long submissionId,
            SubmissionReviewRequestDTO reviewDTO
    ) {
        WikiSubmissionResponseDTO response = wikiService.reviewWikiSubmission(submissionId, reviewDTO);
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