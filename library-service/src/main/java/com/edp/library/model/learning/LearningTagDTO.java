package com.edp.library.model.learning;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningTagDTO {
    @NotNull(message = "Tag ID cannot be null")
    private Long tagId;

    @NotNull(message = "Duration in minutes cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;
}