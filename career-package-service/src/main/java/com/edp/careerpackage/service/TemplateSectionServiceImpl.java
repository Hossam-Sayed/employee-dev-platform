package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.PackageTemplate;
import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.Section;
import com.edp.careerpackage.data.entity.TemplateSectionRequiredTag;
import com.edp.careerpackage.data.repository.PackageTemplateRepository;
import com.edp.careerpackage.data.repository.PackageTemplateSectionRepository;
import com.edp.careerpackage.data.repository.SectionRepository;
import com.edp.careerpackage.mapper.TemplateMapper;
import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateSectionServiceImpl implements TemplateSectionService {

    private final PackageTemplateRepository templateRepository;
    private final SectionRepository sectionRepository;
    private final PackageTemplateSectionRepository templateSectionRepository;
    private final TemplateMapper templateMapper;
    private final TagServiceClient tagServiceClient;

    @Override
    public TemplateSectionResponseDto addSection(Long templateId, TemplateSectionRequestDto request) {
        PackageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + templateId));
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new EntityNotFoundException("Section not found with id " + request.getSectionId()));

        boolean exists = templateSectionRepository.existsByTemplateAndSection(template, section);
        if (exists) {
            throw new DataIntegrityViolationException("Section is already attached to template");
        }

        PackageTemplateSection templateSection = PackageTemplateSection.builder()
                .template(template)
                .section(section)
                .build();

        return templateMapper.toTemplateSectionResponse(templateSectionRepository.save(templateSection));
    }

    @Override
    public void removeSection(Long templateSectionId) {
        PackageTemplateSection templateSection = templateSectionRepository.findById(templateSectionId)
                .orElseThrow(() -> new EntityNotFoundException("TemplateSection not found with id " + templateSectionId));
        templateSectionRepository.delete(templateSection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateSectionRequiredTagResponseDto> listRequiredTags(Long templateSectionId) {
        PackageTemplateSection section = templateSectionRepository.findById(templateSectionId)
                .orElseThrow(() -> new EntityNotFoundException("TemplateSection not found with id " + templateSectionId));

        List<TagResponseDto> response;
        String token = JwtUserContext.getToken();
        try {
            response = tagServiceClient.findAllTagsByIds(section.getRequiredTags().stream().map(TemplateSectionRequiredTag::getTagId).toList(), token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact TagService: " + ex.getMessage());
        }
        return templateMapper.toTemplateSectionRequiredTagResponseList(section.getRequiredTags(),response.stream().map(TagResponseDto::getName).toList());
    }
}
