package com.edp.careerpackage.model.submissionsnapshot;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SubmissionSectionSnapshotResponseDto {
    private Long sectionId;
    private String sectionName;
    private String sectionDescription;
    private List<SubmissionTagSnapshotResponseDto> tags;
}