package com.edp.careerpackage.model.tagprogress;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagPogressResponseDto {
    private Long tagProgressId;
    private String tagName;
    private String criteriaType;
    private Double requiredValue;
    private Double completedValue;
    private String proofUrl;
    private String fileId;
}