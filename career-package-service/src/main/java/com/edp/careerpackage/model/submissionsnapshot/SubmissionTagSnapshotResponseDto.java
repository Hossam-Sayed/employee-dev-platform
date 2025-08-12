package com.edp.careerpackage.model.submissionsnapshot;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionTagSnapshotResponseDto {

    private String tagName;
    private String criteriaType;
    private Double requiredValue;
    private Double submittedValue;
    private String proofLink;
    private String fileId;

}
