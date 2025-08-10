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
public class WikiResponseDTO {
    private Long id; // Wiki ID
    private Long authorId;
    private Long currentSubmissionId;
    private String title; // From current submission
    private String description; // From current submission
    private String documentId; // From current submission
    private SubmissionStatusDTO status; // Status of the current submission
    private List<WikiTagResponseDTO> tags; // Tags from current submission
    private Instant createdAt;
    private Instant updatedAt;
}