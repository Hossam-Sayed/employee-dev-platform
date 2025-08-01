package com.edp.careerpackage.model.careerpackage;

import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageResponseDto {

    private Long id;
    private String department;
    private String position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CareerPackageSectionProgressDto> sections;
    private CareerPackageProgressDto progress;
    private List<SubmissionResponseDto> submissions;
}
