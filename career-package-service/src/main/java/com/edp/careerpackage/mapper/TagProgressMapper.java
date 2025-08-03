package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.CareerPackageTagProgress;
import com.edp.careerpackage.model.tagprogress.TagProgressResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TagProgressMapper {

    @Mappings({
            @Mapping(source = "id", target = "tagProgressId"),
            @Mapping(source = "templateSectionRequiredTag.tag.name", target = "tagName"),
            @Mapping(source = "templateSectionRequiredTag.criteriaType", target = "criteriaType"),
            @Mapping(source = "templateSectionRequiredTag.criteriaMinValue", target = "requiredValue"),
            @Mapping(source = "completedValue", target = "completedValue"),
            @Mapping(source = "proofUrl", target = "proofUrl")
    })
    TagProgressResponseDto toTagProgressResponse(CareerPackageTagProgress tagProgress);
}
