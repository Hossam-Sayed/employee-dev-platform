package com.edp.careerpackage.model.careerpackage;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageCreationRequestDto {

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;
}
