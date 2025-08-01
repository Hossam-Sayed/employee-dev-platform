package com.edp.careerpackage.model.careerpackage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageTagProgressDto {
    private Long tagProgressId;
    private String tagName;
    private String criteriaType;
    private Double requiredValue;
    private Double completedValue;
}