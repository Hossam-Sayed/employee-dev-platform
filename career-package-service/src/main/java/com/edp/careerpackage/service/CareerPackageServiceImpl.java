package com.edp.careerpackage.service;

import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.data.repository.CareerPackageRepository;
import com.edp.careerpackage.data.repository.PackageTemplateRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.careerpackage.CareerPackageResponseDto;
import com.edp.shared.security.jwt.JwtUserContext;
import com.edp.shared.client.auth.model.UserProfileDto;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class CareerPackageServiceImpl implements CareerPackageService {

    private final CareerPackageRepository careerPackageRepository;
    private final PackageTemplateRepository templateRepository;
    private final CareerPackageMapper careerPackageMapper;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional(readOnly = true)
    public CareerPackageResponseDto getCareerPackage() {
        Long userId = JwtUserContext.getUserId();

        CareerPackage careerPackage = careerPackageRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Career package not found"));

        return careerPackageMapper.toCareerPackageResponse(careerPackage);
    }

    @Override
    @Transactional
    public CareerPackageResponseDto createCareerPackage() {
        Long userId = JwtUserContext.getUserId();

        boolean exists = careerPackageRepository.existsByUserIdAndActiveTrue(userId);
        if (exists) {
            throw new DataIntegrityViolationException("Career package already exists for user");
        }

        String token = JwtUserContext.getToken();

        UserProfileDto userProfile;
        try {
            userProfile = authServiceClient.getUserById(userId, token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact AuthService: " + ex.getMessage());
        }
        String department = userProfile.getDepartment();
        String position = userProfile.getPosition();

        PackageTemplate template = templateRepository
                .findByDepartmentAndPosition(department, position)
                .orElseThrow(() -> new EntityNotFoundException("No template found for department and position"));

        CareerPackage careerPackage = CareerPackage.builder()
                .userId(userId)
                .department(template.getDepartment())
                .position(template.getPosition())
                .status(CareerPackageStatus.NOT_STARTED)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();


        CareerPackageProgress progress = CareerPackageProgress.builder()
                .careerPackage(careerPackage)
                .totalProgressPercent(0.0)
                .updatedAt(LocalDateTime.now())
                .build();

        careerPackage.setProgress(progress);

        Set<CareerPackageSectionProgress> sectionProgressList = buildInitialSectionProgress(careerPackage, template);

        careerPackage.setSectionProgressList(sectionProgressList);

        CareerPackage saved = careerPackageRepository.save(careerPackage);
        return careerPackageMapper.toCareerPackageResponse(saved);
    }


    private Set<CareerPackageSectionProgress> buildInitialSectionProgress(
            CareerPackage careerPackage, PackageTemplate template
    ) {
        return template.getSections().stream()
                .map(templateSection -> {
                    CareerPackageSectionProgress sectionProgress = CareerPackageSectionProgress.builder()
                            .careerPackage(careerPackage)
                            .sourceSection(templateSection.getSection())
                            .totalProgressPercent(0.0)
                            .updatedAt(LocalDateTime.now())
                            .build();

                    sectionProgress.setTagProgressList(
                            buildInitialTagProgress(sectionProgress, templateSection)
                    );

                    return sectionProgress;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<CareerPackageTagProgress> buildInitialTagProgress(
            CareerPackageSectionProgress sectionProgress,
            PackageTemplateSection templateSection
    ) {
        return templateSection.getRequiredTags().stream()
                .map(requiredTag -> CareerPackageTagProgress.builder()
                        .careerPackageSectionProgress(sectionProgress)
                        .tagId(requiredTag.getTagId())
                        .criteriaType(requiredTag.getCriteriaType().toString())
                        .requiredValue(requiredTag.getCriteriaMinValue())
                        .completedValue(0.0)
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
