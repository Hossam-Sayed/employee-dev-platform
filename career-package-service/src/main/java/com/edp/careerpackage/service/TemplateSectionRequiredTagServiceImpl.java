package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.Tag;
import com.edp.careerpackage.data.entity.TemplateSectionRequiredTag;
import com.edp.careerpackage.data.repository.PackageTemplateSectionRepository;
import com.edp.careerpackage.data.repository.TagRepository;
import com.edp.careerpackage.data.repository.TemplateSectionRequiredTagRepository;
import com.edp.careerpackage.mapper.TemplateMapper;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateSectionRequiredTagServiceImpl implements TemplateSectionRequiredTagService {

    private final PackageTemplateSectionRepository templateSectionRepository;
    private final TagRepository tagRepository;
    private final TemplateSectionRequiredTagRepository requiredTagRepository;
    private final TemplateMapper templateMapper;

    @Override
    public TemplateSectionRequiredTagResponseDto addRequiredTag(TemplateSectionRequiredTagRequestDto request) {
        PackageTemplateSection section = templateSectionRepository.findById(request.getTemplateSectionId())
                .orElseThrow(() -> new EntityNotFoundException("TemplateSection not found with id " + request.getTemplateSectionId()));

        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id " + request.getTagId()));

        boolean exists = requiredTagRepository.existsByPackageTemplateSectionAndTag(section, tag);
        if (exists) {
            throw new DataIntegrityViolationException("Tag already attached to this section");
        }

        TemplateSectionRequiredTag requiredTag = TemplateSectionRequiredTag.builder()
                .packageTemplateSection(section)
                .tag(tag)
                .criteriaType(request.getCriteriaType())
                .criteriaMinValue(request.getCriteriaMinValue())
                .build();

        return templateMapper.toTemplateSectionRequiredTagResponse(requiredTagRepository.save(requiredTag));
    }

    @Override
    public void removeRequiredTag(Long requiredTagId) {
        TemplateSectionRequiredTag requiredTag = requiredTagRepository.findById(requiredTagId)
                .orElseThrow(() -> new EntityNotFoundException("RequiredTag not found with id " + requiredTagId));
        requiredTagRepository.delete(requiredTag);
    }
}
