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
public class LearningResponseDTO {
    private Long id; // Learning ID
    private Long employeeId; // The employee this learning belongs to
    private Long currentSubmissionId; // ID of the current active submission
    private String title; // From current submission
    private String proofUrl; // From current submission
    private SubmissionStatusDTO status; // Status of the current submission
    private List<LearningTagResponseDTO> tags; // Tags from current submission
    private Instant createdAt; // Learning created date
    private Instant updatedAt; // Learning last updated date
}