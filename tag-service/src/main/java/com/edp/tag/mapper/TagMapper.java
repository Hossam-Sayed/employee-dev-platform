package com.edp.tag.mapper;

import com.edp.tag.data.entity.Tag;
import com.edp.tag.model.tag.TagRequestDto;
import com.edp.tag.model.tag.TagResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    TagResponseDto toTagResponse(Tag tag);


    List<TagResponseDto> toTagsResponse(List<Tag> tags);

    List<TagResponseDto> toTagResponseList(List<Tag> tags);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name")
    })
    Tag toTag(TagRequestDto request);
}
