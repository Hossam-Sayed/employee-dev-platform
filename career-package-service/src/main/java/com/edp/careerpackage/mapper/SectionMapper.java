package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.Section;
import com.edp.careerpackage.model.SectionRequestDto;
import com.edp.careerpackage.model.SectionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description")
    })
    SectionResponseDto toSectionResponse(Section section);

    List<SectionResponseDto> toSectionResponseList(List<Section> sections);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description")
    })
    Section toSection(SectionRequestDto request);
}
