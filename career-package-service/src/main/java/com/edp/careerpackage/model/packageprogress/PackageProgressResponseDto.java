package com.edp.careerpackage.model.packageprogress;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageProgressResponseDto {
    private Double totalProgressPercent;
    private LocalDateTime updatedAt;
}
