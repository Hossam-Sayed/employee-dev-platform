package com.edp.careerpackage.model.tagprogress;

import com.edp.careerpackage.data.enums.CriteriaType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagProgressResponseDto {

    private Long tagProgressId;

    private String tagName;

    private CriteriaType criteriaType;

    private Double requiredValue;

    private Double completedValue;

    private String proofUrl;
}
