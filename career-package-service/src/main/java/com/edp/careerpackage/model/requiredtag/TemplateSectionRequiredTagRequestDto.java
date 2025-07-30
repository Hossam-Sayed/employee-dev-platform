package com.edp.careerpackage.model.requiredtag;

import com.edp.careerpackage.data.enums.CriteriaType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionRequiredTagRequestDto {

    @NotNull(message = "TagId is required")
    private Long tagId;

    @NotNull(message = "Criteria type is required")
    private CriteriaType criteriaType;

    @NotNull(message = "Criteria min value is required")
    private Double criteriaMinValue;
}
