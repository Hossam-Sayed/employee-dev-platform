package com.edp.careerpackage.model;

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
