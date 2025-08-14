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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<CareerPackageTagProgress> tagsProgressList = sectionProgress.getTagProgressList();
        List<Long> tagIds = tagsProgressList.stream()
                .map(CareerPackageTagProgress::getTagId)
                .toList();

        String token = JwtUserContext.getToken();
        try {
            List<TagResponseDto> tagResponses = tagServiceClient.findAllTagsByIds(tagIds, token);

            Map<Long, String> tagIdToNameMap = tagResponses.stream()
                    .collect(Collectors.toMap(TagResponseDto::getId, TagResponseDto::getName));

            List<TagPogressResponseDto> result = new ArrayList<>();
            for (CareerPackageTagProgress tagProgress : tagsProgressList) {
                String tagName = tagIdToNameMap.get(tagProgress.getTagId());
                if (tagName != null) {
                    result.add(careerPackageMapper.toCareerPackageTagProgress(tagProgress, tagName));
                }
            }
            return result;

        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact TagService: " + ex.getMessage());
        }
    }
}