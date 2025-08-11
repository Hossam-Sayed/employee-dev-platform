package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.SubmissionTagSnapshot;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionTagSnapshotMapper {

    @Mappings({
            @Mapping(source = "tagId", target = "tagId"),
            @Mapping(source = "sourceSectionId", target = "sourceSectionId"),
            @Mapping(source = "criteriaType", target = "criteriaType"),
            @Mapping(source = "requiredValue", target = "requiredValue"),
            @Mapping(source = "submittedValue", target = "submittedValue"),
            @Mapping(source = "proofLink", target = "proofLink"),
            @Mapping(source = "fileId", target = "fileId")

    })
    SubmissionTagSnapshotResponseDto toSnapshotResponseDto(SubmissionTagSnapshot snapshot);

    List<SubmissionTagSnapshotResponseDto> toSnapshotResponseDtoList(List<SubmissionTagSnapshot> snapshots);
}
