package com.edp.careerpackage.model.careerpackage;

import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.model.packageprogress.PackageProgressResponseDto;
import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
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
    private CareerPackageStatus status;

    private List<SectionProgressResponseDto> sections;
    private PackageProgressResponseDto progress;
    private List<SubmissionResponseDto> submissions;
}
