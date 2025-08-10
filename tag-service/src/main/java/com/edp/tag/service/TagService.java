package com.edp.tag.service;

import com.edp.tag.model.tag.TagRequestDto;
import com.edp.tag.model.tag.TagResponseDto;

import java.util.List;

public interface TagService {

    List<TagResponseDto> searchTags(String query);

    TagResponseDto createTag(TagRequestDto request);
}
