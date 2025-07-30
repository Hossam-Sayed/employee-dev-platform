package com.edp.careerpackage.model.requiredtag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionRequiredTagResponseDto {

    private Long id;
    private String tagName;
    private String criteriaType;
    private Double criteriaMinValue;
}
