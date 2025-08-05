package com.edp.library.model.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningTagResponseDTO {
    private Long id;
    private Long tagId;
    private String tagName;
    private Integer durationMinutes;
    private Instant createdAt;
}