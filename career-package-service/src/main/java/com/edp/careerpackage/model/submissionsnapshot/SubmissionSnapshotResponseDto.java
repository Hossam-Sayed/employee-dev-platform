package com.edp.careerpackage.model.submissionsnapshot;

import com.edp.shared.client.auth.model.UserProfileDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class SubmissionSnapshotResponseDto {
    private Long submissionId;
    private UserProfileDto user;
    private String department;
    private String position;
    private String status;
    private String comment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private List<SubmissionSectionSnapshotResponseDto> sections;
}