package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.model.careerpackage.*;
import com.edp.careerpackage.model.packageprogress.PackageProgressResponseDto;
import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CareerPackageMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "template.department", target = "department"),
            @Mapping(source = "template.position", target = "position"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt"),
            @Mapping(source = "sectionProgressList", target = "sections"),
            @Mapping(source = "progress", target = "progress"),
            @Mapping(source = "submissions", target = "submissions")
    })
    CareerPackageResponseDto toCareerPackageResponse(CareerPackage careerPackage);

    @Mappings({
            @Mapping(source = "totalProgressPercent", target = "totalProgressPercent"),
            @Mapping(source = "updatedAt", target = "updatedAt")
    })
    PackageProgressResponseDto toCareerPackageProgressDto(CareerPackageProgress progress);

    @Mappings({
            @Mapping(source = "id", target = "sectionProgressId"),
            @Mapping(source = "packageTemplateSection.section.name", target = "sectionName"),
            @Mapping(source = "packageTemplateSection.section.description", target = "sectionDescription"),
            @Mapping(source = "totalProgressPercent", target = "sectionProgressPercent"),
            @Mapping(source = "tagProgressList", target = "tags")
    })
    SectionProgressResponseDto toCareerPackageSectionProgress(CareerPackageSectionProgress sectionProgress);

    @Mappings({
            @Mapping(source = "id", target = "tagProgressId"),
            @Mapping(source = "templateSectionRequiredTag.tag.name", target = "tagName"),
            @Mapping(source = "templateSectionRequiredTag.criteriaType", target = "criteriaType"),
            @Mapping(source = "templateSectionRequiredTag.criteriaMinValue", target = "requiredValue"),
            @Mapping(source = "completedValue", target = "completedValue"),
            @Mapping(source = "proofUrl", target = "proofUrl")
    })
    TagPogressResponseDto toCareerPackageTagProgress(CareerPackageTagProgress tagProgress);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "submittedAt", target = "submittedAt"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "comment", target = "comment"),
            @Mapping(source = "reviewedAt", target = "reviewedAt")
    })
    SubmissionResponseDto toSubmissionResponseDto(Submission submission);

    List<SectionProgressResponseDto> toCareerPackageSectionProgressList(Collection<CareerPackageSectionProgress> list);
    List<TagPogressResponseDto> toCareerPackageTagProgressList(Collection<CareerPackageTagProgress> list);
    List<SubmissionResponseDto> toSubmissionResponseDtoList(List<Submission> submissions);
}
