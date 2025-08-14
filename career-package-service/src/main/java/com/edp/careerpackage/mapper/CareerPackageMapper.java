package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.model.careerpackage.*;
import com.edp.careerpackage.model.packageprogress.PackageProgressResponseDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CareerPackageMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt"),
            @Mapping(source = "sectionProgressList", target = "sections"),
            @Mapping(source = "progress", target = "progress"),
            @Mapping(source = "submissions", target = "submissions"),
            @Mapping(source = "status", target = "status")
    })
    CareerPackageResponseDto toCareerPackageResponse(CareerPackage careerPackage);

    @Mappings({
            @Mapping(source = "totalProgressPercent", target = "totalProgressPercent"),
            @Mapping(source = "updatedAt", target = "updatedAt")
    })
    PackageProgressResponseDto toCareerPackageProgressDto(CareerPackageProgress progress);

    @Mappings({
            @Mapping(source = "id", target = "sectionProgressId"),
            @Mapping(source = "sourceSection.name", target = "sectionName"),
            @Mapping(source = "sourceSection.description", target = "sectionDescription"),
            @Mapping(source = "totalProgressPercent", target = "sectionProgressPercent"),
            @Mapping(target = "tags",ignore = true)
    })
    SectionProgressResponseDto toCareerPackageSectionProgress(CareerPackageSectionProgress sectionProgress);

    @Mappings({
            @Mapping(source = "tagProgress.id", target = "tagProgressId"),
            @Mapping(source = "tagName", target = "tagName"),
            @Mapping(source = "tagProgress.criteriaType", target = "criteriaType"),
            @Mapping(source = "tagProgress.requiredValue", target = "requiredValue"),
            @Mapping(source = "tagProgress.completedValue", target = "completedValue"),
            @Mapping(source = "tagProgress.proofUrl", target = "proofUrl"),
            @Mapping(source = "tagProgress.fileId", target = "fileId")
    })
    TagPogressResponseDto toCareerPackageTagProgress(CareerPackageTagProgress tagProgress,String tagName);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "submittedAt", target = "submittedAt"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "comment", target = "comment"),
            @Mapping(source = "reviewedAt", target = "reviewedAt")
    })
    SubmissionResponseDto toSubmissionResponseDto(Submission submission);

    List<SectionProgressResponseDto> toCareerPackageSectionProgressList(Collection<CareerPackageSectionProgress> list);
    List<SubmissionResponseDto> toSubmissionResponseDtoList(Collection<Submission> submissions);
}
