package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.PackageTemplate;
import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.TemplateSectionRequiredTag;

import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.ArrayList;
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
            @Mapping(target = "requiredTags",ignore = true)
    })
    TemplateSectionResponseDto toTemplateSectionResponse(PackageTemplateSection templateSection);

    @Mappings({
            @Mapping(source = "requiredTag.id", target = "id"),
            @Mapping(source = "tagName", target = "tagName"),
            @Mapping(source = "requiredTag.criteriaType", target = "criteriaType"),
            @Mapping(source = "requiredTag.criteriaMinValue", target = "criteriaMinValue")
    })
    TemplateSectionRequiredTagResponseDto toTemplateSectionRequiredTagResponse(TemplateSectionRequiredTag requiredTag, String tagName);

    List<TemplateResponseDto> toTemplateResponseList(List<PackageTemplate> templates);

    List<TemplateSectionResponseDto> toTemplateSectionResponseList(List<PackageTemplateSection> sections);


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


    //Custom method to map TemplateSectionRequiredTag list + Tag name list parameter by iterating on the single mapper
    default List<TemplateSectionRequiredTagResponseDto> toTemplateSectionRequiredTagResponseList(
            List<TemplateSectionRequiredTag> requiredTags, List<String> tagNames
    ) {
        if (requiredTags == null || tagNames == null || requiredTags.size() != tagNames.size()) {
            throw new IllegalArgumentException("Lists must have the same size");
        }

        List<TemplateSectionRequiredTagResponseDto> result = new ArrayList<>();
        for (int i = 0; i < requiredTags.size(); i++) {
            result.add(toTemplateSectionRequiredTagResponse(requiredTags.get(i), tagNames.get(i)));
        }
        return result;
    }
}

