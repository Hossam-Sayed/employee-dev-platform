package com.edp.careerpackage.model.template;

import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDetailResponseDto {

    private Long id;
    private String department;
    private String position;
    private LocalDateTime createdAt;

    private List<TemplateSectionResponseDto> sections;
}
