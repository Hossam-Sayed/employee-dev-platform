package com.edp.careerpackage.model.careerpackage;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageSectionProgressDto {

    private Long sectionProgressId;
    private String sectionName;
    private String sectionDescription;
    private Double sectionProgressPercent;
    private List<CareerPackageTagProgressDto> tags;
}