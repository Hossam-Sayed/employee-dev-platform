package com.edp.library.model;

import com.edp.library.model.enums.SubmissionStatusDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionReviewRequestDTO {
    @NotNull(message = "Status cannot be null")
    private SubmissionStatusDTO status; // Should be APPROVED or REJECTED

    private String reviewerComment;
}