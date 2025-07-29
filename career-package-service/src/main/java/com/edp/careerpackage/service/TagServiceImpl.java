package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.Tag;
import com.edp.careerpackage.data.repository.TagRepository;
import com.edp.careerpackage.mapper.TagMapper;
import com.edp.careerpackage.model.TagRequestDto;
import com.edp.careerpackage.model.TagResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return tagMapper.toTagResponse(tagRepository.save(tag));
    }
}
