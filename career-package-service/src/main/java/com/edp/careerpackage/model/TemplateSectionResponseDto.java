package com.edp.careerpackage.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionResponseDto {

    private Long id;
    private String name;
    private String description;

    private List<TemplateSectionRequiredTagResponseDto> requiredTags;
}
