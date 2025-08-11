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
    List<TagResponseDto> searchTags(
            @RequestParam(name="query",required = false) String query,
            @RequestHeader("Authorization") String token
    );

    @PostMapping("/api/tags")
    TagResponseDto createTag(
            @RequestBody TagRequestDto request,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/tags/{id}")
    TagResponseDto findTagById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/tags/by-ids")
    List<TagResponseDto> findAllTagsByIds(
            @RequestParam("ids") List<Long> tagIds,
            @RequestHeader("Authorization") String token
            );
}
