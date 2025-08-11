package com.edp.tag.service;

import com.edp.shared.security.jwt.JwtUserContext;
import com.edp.tag.data.entity.Tag;
import com.edp.tag.data.repository.TagRepository;
import com.edp.tag.mapper.TagMapper;
import com.edp.tag.model.tag.TagRequestDto;
import com.edp.tag.model.tag.TagResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDto> searchTags(String query) {
        String q = (query == null || query.isBlank()) ? "" : query;
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q);
        return tagMapper.toTagResponseList(tags);
    }

    @Override
    public TagResponseDto createTag(TagRequestDto request) {
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DataIntegrityViolationException("Tag with the same name already exists");
        }
        Tag tag = tagMapper.toTag(request);
        tag.setCreatedBy(JwtUserContext.getUserId());
        return tagMapper.toTagResponse(tagRepository.save(tag));
    }

    @Override
    public TagResponseDto findTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag doesn't exist"));
        return tagMapper.toTagResponse(tag);
    }

    @Override
    public List<TagResponseDto> findAllTagsByIds(List<Long> tagIds) {
        if (tagIds.isEmpty()) return Collections.emptyList();
        List<Tag> tagsList = tagRepository.findAllById(tagIds);
        if (tagsList.isEmpty()) throw new EntityNotFoundException("No tag exist");
        return tagMapper.toTagsResponse(tagsList);
    }
}
