package com.edp.library.model.wiki;

import com.edp.library.model.enums.SubmissionStatusDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiSubmissionResponseDTO {
    private Long id; // Submission ID
    private Long wikiId;
    private String title;
    private String description;
    private String documentUrl;
    private SubmissionStatusDTO status;
    private String reviewerComment;
    private Long submitterId;
    private Instant submittedAt;
    private Long reviewerId;
    private Instant reviewedAt;
    private List<WikiTagResponseDTO> tags;
}