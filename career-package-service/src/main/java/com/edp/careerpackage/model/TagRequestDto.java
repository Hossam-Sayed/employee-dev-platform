package com.edp.careerpackage.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestDto {

    @NotBlank(message = "Tag name is required")
    private String name;
}
