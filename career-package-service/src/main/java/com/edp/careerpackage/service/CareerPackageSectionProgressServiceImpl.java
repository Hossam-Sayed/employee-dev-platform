package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.requiredtag.TemplateSectionRequiredTagResponseDto;
import com.edp.careerpackage.model.sectionprogress.SectionProgressResponseDto;
import com.edp.careerpackage.data.repository.CareerPackageSectionProgressRepository;

import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
import com.edp.shared.security.jwt.JwtUserContext;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CareerPackageSectionProgressServiceImpl implements CareerPackageSectionProgressService {

    private final CareerPackageSectionProgressRepository sectionProgressRepository;
    private final CareerPackageMapper careerPackageMapper;
    private final TagServiceClient tagServiceClient;

    @Override
    @Transactional(readOnly = true)
    public SectionProgressResponseDto getSectionProgress(Long sectionProgressId) {
        CareerPackageSectionProgress sectionProgress = sectionProgressRepository.findById(sectionProgressId)
                .orElseThrow(() -> new EntityNotFoundException("SectionProgress not found with id " + sectionProgressId));

        Long currentUserId = JwtUserContext.getUserId();
        CareerPackage careerPackage = sectionProgress.getCareerPackage();

        if (!careerPackage.getUserId().equals(currentUserId)) {
            throw new AuthenticationException("User not authorized to view this section progress") {
            };
        }
        return careerPackageMapper.toCareerPackageSectionProgress(sectionProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagPogressResponseDto> listTagsProgress(Long sectionProgressId) {
        CareerPackageSectionProgress sectionProgress = sectionProgressRepository.findById(sectionProgressId)
                .orElseThrow(() -> new EntityNotFoundException("Section Progress not found with id " + sectionProgressId));

        Long currentUserId = JwtUserContext.getUserId();
        CareerPackage careerPackage = sectionProgress.getCareerPackage();

        if (!careerPackage.getUserId().equals(currentUserId)) {
            throw new AuthenticationException("User not authorized to view this section tags progress") {
            };
        }
        List<TagResponseDto> response;
        String token = JwtUserContext.getToken();
        try {
            response = tagServiceClient.findAllTagsByIds(sectionProgress.getTagProgressList().stream().map(CareerPackageTagProgress::getTagId).toList(), token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact TagService: " + ex.getMessage());
        }
        return careerPackageMapper.toSectionTagsProgressResponseList(sectionProgress.getTagProgressList(),response.stream().map(TagResponseDto::getName).toList());
    }
}