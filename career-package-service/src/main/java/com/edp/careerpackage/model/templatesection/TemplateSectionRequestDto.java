package com.edp.careerpackage.model.templatesection;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionRequestDto {

    @NotNull(message = "SectionId is required")
    private Long sectionId;
}
