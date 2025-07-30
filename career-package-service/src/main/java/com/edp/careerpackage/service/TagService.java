package com.edp.careerpackage.service;

import com.edp.careerpackage.model.tag.TagRequestDto;
import com.edp.careerpackage.model.tag.TagResponseDto;

import java.util.List;

public interface TagService {

    List<TagResponseDto> searchTags(String query);

    TagResponseDto createTag(TagRequestDto request);
}
