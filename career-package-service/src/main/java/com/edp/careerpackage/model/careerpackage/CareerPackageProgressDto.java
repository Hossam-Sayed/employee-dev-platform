package com.edp.careerpackage.model.careerpackage;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageProgressDto {
    private Double totalProgressPercent;
    private LocalDateTime updatedAt;
}
