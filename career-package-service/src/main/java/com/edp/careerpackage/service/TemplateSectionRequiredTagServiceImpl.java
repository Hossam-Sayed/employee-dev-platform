package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.TemplateSectionRequiredTag;
import com.edp.careerpackage.data.repository.PackageTemplateSectionRepository;
import com.edp.careerpackage.data.repository.TemplateSectionRequiredTagRepository;
import com.edp.careerpackage.mapper.TemplateMapper;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagRequestDto;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;

import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
import com.edp.shared.security.jwt.JwtUserContext;
import feign.FeignException;
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
    private final TemplateSectionRequiredTagRepository requiredTagRepository;
    private final TemplateMapper templateMapper;
    private final TagServiceClient tagServiceClient;

    @Override
    public TemplateSectionRequiredTagResponseDto addRequiredTag(TemplateSectionRequiredTagRequestDto request) {
        PackageTemplateSection section = templateSectionRepository.findById(request.getTemplateSectionId())
                .orElseThrow(() -> new EntityNotFoundException("TemplateSection not found with id " + request.getTemplateSectionId()));


        TagResponseDto response;
        String token = JwtUserContext.getToken();
        try {
            response = tagServiceClient.findTagById(request.getTagId(), token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact TagService: " + ex.getMessage());
        }

        boolean exists = requiredTagRepository.existsByPackageTemplateSectionAndTagId(section, response.getId());
        if (exists) {
            throw new DataIntegrityViolationException("Tag already attached to this section");
        }

        TemplateSectionRequiredTag requiredTag = TemplateSectionRequiredTag.builder()
                .packageTemplateSection(section)
                .tagId(response.getId())
                .criteriaType(request.getCriteriaType())
                .criteriaMinValue(request.getCriteriaMinValue())
                .build();

        return templateMapper.toTemplateSectionRequiredTagResponse(requiredTagRepository.save(requiredTag), response.getName());
    }

    @Override
    public void removeRequiredTag(Long requiredTagId) {
        TemplateSectionRequiredTag requiredTag = requiredTagRepository.findById(requiredTagId)
                .orElseThrow(() -> new EntityNotFoundException("RequiredTag not found with id " + requiredTagId));
        requiredTagRepository.delete(requiredTag);
    }
}
