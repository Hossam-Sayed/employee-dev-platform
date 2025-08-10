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
public class BlogSubmissionResponseDTO {
    private Long id; // Submission ID
    private Long blogId;
    private String title;
    private String description;
    private String documentId;
    private SubmissionStatusDTO status;
    private String reviewerComment;
    private Long submitterId;
    private Instant submittedAt;
    private Long reviewerId;
    private Instant reviewedAt;
    private List<BlogTagResponseDTO> tags;
}