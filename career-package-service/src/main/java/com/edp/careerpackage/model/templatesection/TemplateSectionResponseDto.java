package com.edp.careerpackage.model.templatesection;

import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
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
