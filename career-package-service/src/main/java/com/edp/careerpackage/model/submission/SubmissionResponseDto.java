package com.edp.careerpackage.model.submission;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponseDto {
    private Long id;
    private LocalDateTime submittedAt;
    private String status;
    private String comment;
    private LocalDateTime reviewedAt;
}
