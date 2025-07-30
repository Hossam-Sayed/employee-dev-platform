package com.edp.careerpackage.mapper;

import com.edp.careerpackage.data.entity.Tag;
import com.edp.careerpackage.model.tag.TagRequestDto;
import com.edp.careerpackage.model.tag.TagResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name")
    })
    TagResponseDto toTagResponse(Tag tag);

    List<TagResponseDto> toTagResponseList(List<Tag> tags);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name")
    })
    Tag toTag(TagRequestDto request);
}
