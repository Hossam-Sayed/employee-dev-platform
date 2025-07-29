package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.Section;
import com.edp.careerpackage.data.repository.SectionRepository;
import com.edp.careerpackage.mapper.SectionMapper;
import com.edp.careerpackage.model.SectionRequestDto;
import com.edp.careerpackage.model.SectionResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SectionResponseDto> searchSections(String query) {
        String q = (query == null || query.isBlank()) ? "" : query;
        List<Section> sections = sectionRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q);
        return sectionMapper.toSectionResponseList(sections);
    }

    @Override
    public SectionResponseDto createSection(SectionRequestDto request) {
        if (sectionRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DataIntegrityViolationException("Section with the same name already exists");
        }
        Section section = sectionMapper.toSection(request);
        return sectionMapper.toSectionResponse(sectionRepository.save(section));
    }
}
