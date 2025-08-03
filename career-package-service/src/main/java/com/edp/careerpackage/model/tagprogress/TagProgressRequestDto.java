package com.edp.careerpackage.model.tagprogress;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagProgressRequestDto {

    @NotNull(message = "Completed value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Completed value must be positive")
    private Double completedValue;

    @NotNull(message = "Proof URL is required")
    @Size(max = 1000, message = "Proof URL must be at most 1000 characters")
    private String proofUrl;
}
