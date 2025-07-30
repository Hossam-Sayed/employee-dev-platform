package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.PackageTemplate;
import com.edp.careerpackage.data.repository.PackageTemplateRepository;
import com.edp.careerpackage.mapper.TemplateMapper;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.templatesection.TemplateSectionResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final PackageTemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TemplateResponseDto> listTemplates(String department, String position, int page, int size) {
        String dept = (department == null || department.isBlank()) ? "" : department;
        String pos = (position == null || position.isBlank()) ? "" : position;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return templateRepository
                .findByDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(dept, pos, pageable)
                .map(templateMapper::toTemplateResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateDetailResponseDto getTemplateById(Long id) {
        PackageTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + id));
        return templateMapper.toTemplateDetailResponse(template);
    }

    @Override
    public TemplateDetailResponseDto createTemplate(TemplateRequestDto request) {
        if (templateRepository.existsByDepartmentIgnoreCaseAndPositionIgnoreCase(request.getDepartment(), request.getPosition())) {
            throw new DataIntegrityViolationException("Template already exists for department/position");
        }
        PackageTemplate template = templateMapper.toPackageTemplate(request);
        template.setCreatedAt(LocalDateTime.now());
        return templateMapper.toTemplateDetailResponse(templateRepository.save(template));
    }

    @Override
    public TemplateDetailResponseDto updateTemplate(Long id, TemplateUpdateRequestDto request) {
        PackageTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + id));
        templateMapper.updatePackageTemplate(template, request);
        template.setUpdatedAt(LocalDateTime.now());
        return templateMapper.toTemplateDetailResponse(templateRepository.save(template));
    }

    @Override
    public void deleteTemplate(Long id) {
        PackageTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + id));
        templateRepository.delete(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateSectionResponseDto> listSections(Long templateId) {
        PackageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id " + templateId));
        return templateMapper.toTemplateSectionResponseList(template.getSections());
    }

}
