package com.edp.careerpackage.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionRequestDto {

    @NotBlank(message = "Section name is required")
    private String name;

    private String description;
}
