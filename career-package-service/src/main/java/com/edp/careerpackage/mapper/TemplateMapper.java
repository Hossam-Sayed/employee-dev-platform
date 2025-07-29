package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.PackageTemplate;
import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.TemplateSectionRequiredTag;

import com.edp.careerpackage.model.TemplateRequestDto;
import com.edp.careerpackage.model.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.TemplateResponseDto;
import com.edp.careerpackage.model.TemplateDetailResponseDto;
import com.edp.careerpackage.model.TemplateSectionResponseDto;
import com.edp.careerpackage.model.TemplateSectionRequiredTagResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    TemplateResponseDto toTemplateResponse(PackageTemplate template);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "sections", target = "sections")
    })
    TemplateDetailResponseDto toTemplateDetailResponse(PackageTemplate template);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "section.name", target = "name"),
            @Mapping(source = "section.description", target = "description"),
            @Mapping(source = "requiredTags", target = "requiredTags")
    })
    TemplateSectionResponseDto toTemplateSectionResponse(PackageTemplateSection templateSection);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "tag.name", target = "tagName"),
            @Mapping(source = "criteriaType", target = "criteriaType"),
            @Mapping(source = "criteriaMinValue", target = "criteriaMinValue")
    })
    TemplateSectionRequiredTagResponseDto toTemplateSectionRequiredTagResponse(TemplateSectionRequiredTag requiredTag);

    List<TemplateResponseDto> toTemplateResponseList(List<PackageTemplate> templates);
    List<TemplateSectionResponseDto> toTemplateSectionResponseList(List<PackageTemplateSection> sections);
    List<TemplateSectionRequiredTagResponseDto> toTemplateSectionRequiredTagResponseList(List<TemplateSectionRequiredTag> requiredTags);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "sections", ignore = true)
    })
    PackageTemplate toPackageTemplate(TemplateRequestDto request);

    @Mappings({
            @Mapping(target = "department", source = "department"),
            @Mapping(target = "position", source = "position")
    })
    void updatePackageTemplate(@MappingTarget PackageTemplate template, TemplateUpdateRequestDto request);
}
