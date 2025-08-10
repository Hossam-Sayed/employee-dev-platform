package com.edp.tag.controller;

import com.edp.tag.model.tag.TagRequestDto;
import com.edp.tag.model.tag.TagResponseDto;
import com.edp.tag.service.TagService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    @Override
    public ResponseEntity<List<TagResponseDto>> searchTags(String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    @Override
    public ResponseEntity<TagResponseDto> createTag(TagRequestDto request, UriComponentsBuilder uriBuilder) {
        TagResponseDto created = tagService.createTag(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/tags/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }
}
