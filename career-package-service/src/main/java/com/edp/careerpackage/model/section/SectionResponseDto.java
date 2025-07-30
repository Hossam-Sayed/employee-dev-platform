package com.edp.careerpackage.model.section;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionResponseDto {

    private Long id;
    private String name;
    private String description;
}
