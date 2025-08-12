package com.edp.careerpackage.model.submission;

import com.edp.shared.client.auth.model.UserProfileDto;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagedSubmissionResponseDto {
    private Long id;
    private LocalDateTime submittedAt;
    private String status;
    private String comment;
    private LocalDateTime reviewedAt;
    private UserProfileDto user;
}