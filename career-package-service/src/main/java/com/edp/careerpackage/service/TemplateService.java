package com.edp.careerpackage.service;

import com.edp.careerpackage.model.TemplateRequestDto;
import com.edp.careerpackage.model.TemplateUpdateRequestDto;
import com.edp.careerpackage.model.TemplateResponseDto;
import com.edp.careerpackage.model.TemplateDetailResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TemplateService {

    Page<TemplateResponseDto> listTemplates(String department, String position, int page, int size);

    TemplateDetailResponseDto getTemplateById(Long id);

    TemplateDetailResponseDto createTemplate(TemplateRequestDto request);

    TemplateDetailResponseDto updateTemplate(Long id, TemplateUpdateRequestDto request);

    void deleteTemplate(Long id);
}
