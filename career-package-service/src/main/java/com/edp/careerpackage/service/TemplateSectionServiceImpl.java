package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.PackageTemplate;
import com.edp.careerpackage.data.entity.PackageTemplateSection;
import com.edp.careerpackage.data.entity.Section;
import com.edp.careerpackage.data.repository.PackageTemplateRepository;
import com.edp.careerpackage.data.repository.PackageTemplateSectionRepository;
import com.edp.careerpackage.data.repository.SectionRepository;
import com.edp.careerpackage.mapper.TemplateMapper;
import com.edp.careerpackage.model.templatesection.TemplateSectionRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;

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

    @Override
    @Transactional(readOnly = true)
    public List<TemplateSectionResponseDto> listSections(Long templateId) {
        PackageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + templateId));
        return templateMapper.toTemplateSectionResponseList(template.getSections());
    }

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
    public void removeSection(Long templateId, Long templateSectionId) {
        PackageTemplateSection templateSection = templateSectionRepository.findById(templateSectionId)
                .orElseThrow(() -> new EntityNotFoundException("TemplateSection not found with id " + templateSectionId));

        if (!templateSection.getTemplate().getId().equals(templateId)) {
            throw new EntityNotFoundException("TemplateSection does not belong to the given template");
        }
        templateSectionRepository.delete(templateSection);
    }
}
