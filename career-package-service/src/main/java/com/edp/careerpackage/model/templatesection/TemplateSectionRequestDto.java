package com.edp.careerpackage.model.templatesection;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionRequestDto {

    @NotNull(message = "TemplateId is required")
    private Long templateId;

    @NotNull(message = "SectionId is required")
    private Long sectionId;
}
