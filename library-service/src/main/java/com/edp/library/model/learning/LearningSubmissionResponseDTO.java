package com.edp.library.model.learning;

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
public class LearningSubmissionResponseDTO {
    private Long id; // Submission ID
    private Long learningId;
    private String title;
    private String proofUrl;
    private SubmissionStatusDTO status;
    private String reviewerComment;
    private Long submitterId;
    private Instant submittedAt;
    private Long reviewerId;
    private Instant reviewedAt;
    private List<LearningTagResponseDTO> tags; // All tags for this specific submission
}