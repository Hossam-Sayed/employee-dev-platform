package com.edp.library.model.blog;

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
public class BlogResponseDTO {
    private Long id; // Blog ID
    private Long authorId;
    private Long currentSubmissionId;
    private String title; // From current submission
    private String description; // From current submission
    private String documentUrl; // From current submission
    private SubmissionStatusDTO status; // Status of the current submission
    private List<BlogTagResponseDTO> tags; // Tags from current submission
    private Instant createdAt;
    private Instant updatedAt;
}