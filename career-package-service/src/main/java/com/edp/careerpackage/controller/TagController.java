package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.tag.TagRequestDto;
import com.edp.careerpackage.model.tag.TagResponseDto;
import com.edp.careerpackage.service.TagService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController implements TagControllerApi {

    private final TagService tagService;

    @Override
    public ResponseEntity<List<TagResponseDto>> searchTags(String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    @Override
    public ResponseEntity<TagResponseDto> createTag(TagRequestDto request, UriComponentsBuilder uriBuilder) {
        TagResponseDto created = tagService.createTag(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/admin/tags/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }
}
