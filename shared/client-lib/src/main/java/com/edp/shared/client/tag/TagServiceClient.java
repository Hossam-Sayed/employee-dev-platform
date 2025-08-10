package com.edp.shared.client.tag;

import com.edp.shared.client.tag.model.TagRequestDto;
import com.edp.shared.client.tag.model.TagResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@FeignClient(name = "tag-service", url = "${tag.service.url}")
public interface TagServiceClient {

    @GetMapping("/api/tags")
    ResponseEntity<List<TagResponseDto>> searchTags(
            @RequestParam(required = false) String query
    );

    @PostMapping("/api/tags")
    ResponseEntity<TagResponseDto> createTag(
            @RequestBody TagRequestDto request,
            UriComponentsBuilder uriBuilder
    );
}
