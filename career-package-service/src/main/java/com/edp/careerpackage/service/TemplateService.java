package com.edp.careerpackage.service;

import com.edp.careerpackage.model.template.TemplateRequestDto;
import com.edp.careerpackage.model.template.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.template.TemplateResponseDto;
import com.edp.careerpackage.model.template.TemplateDetailResponseDto;
import org.springframework.data.domain.Page;

public interface TemplateService {

    Page<TemplateResponseDto> listTemplates(String department, String position, int page, int size);

    TemplateDetailResponseDto getTemplateById(Long id);

    TemplateDetailResponseDto createTemplate(TemplateRequestDto request);

    TemplateDetailResponseDto updateTemplate(Long id, TemplateUpdateRequestDto request);

    void deleteTemplate(Long id);
}
