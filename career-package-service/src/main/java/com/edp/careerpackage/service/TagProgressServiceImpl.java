package com.edp.careerpackage.service;

import com.edp.shared.client.file.FileServiceClient;
import com.edp.shared.client.file.model.FileResponseDto;
import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.data.repository.CareerPackageTagProgressRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import com.edp.shared.security.jwt.JwtUserContext;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TagProgressServiceImpl implements TagProgressService {

    private final CareerPackageTagProgressRepository tagProgressRepository;
    private final CareerPackageMapper mapper;
    private final FileServiceClient fileServiceClient;


    @Override
    @Transactional
    public TagPogressResponseDto updateTagProgress(Long tagProgressId, Double completedValue, String proofUrl, MultipartFile file) {
        Long currentUserId = JwtUserContext.getUserId();

        CareerPackageTagProgress tagProgress = tagProgressRepository.findById(tagProgressId)
                .orElseThrow(() -> new EntityNotFoundException("Tag progress not found"));

        CareerPackage careerPackage = tagProgress.getCareerPackageSectionProgress().getCareerPackage();

        if (!careerPackage.getUserId().equals(currentUserId)) {
            throw new AuthenticationException("User not authorized to update this tag progress") {
            };
        }

        CareerPackageStatus status = careerPackage.getStatus();
        if (status == CareerPackageStatus.SUBMITTED || status == CareerPackageStatus.APPROVED) {
            throw new DataIntegrityViolationException("Cannot update tag progress for a submitted or approved package");
        }

        //the min value required for completing a tag is the max value to be submitted as progress for this tag
        double maxValue = tagProgress.getRequiredValue();
        double boundValue = Math.min(completedValue, maxValue);

        tagProgress.setCompletedValue(boundValue);
        tagProgress.setProofUrl(proofUrl);

        if (file != null && !file.isEmpty()) {
            String token = JwtUserContext.getToken();
            try {
                FileResponseDto response = fileServiceClient.uploadFile(file, token, false).getBody();
                tagProgress.setFileId(response.getId());
            } catch (FeignException ex) {
                throw new IllegalStateException("Failed to contact FileService: " + ex.getMessage());
            }
        }

        CareerPackageSectionProgress sectionProgress = tagProgress.getCareerPackageSectionProgress();
        recalculateSectionProgress(sectionProgress);
        recalculatePackageProgress(careerPackage);

        return mapper.toCareerPackageTagProgress(tagProgress);
    }

    private void recalculateSectionProgress(CareerPackageSectionProgress sectionProgress) {
        int totalTags = sectionProgress.getTagProgressList().size();
        if (totalTags == 0) {
            sectionProgress.setTotalProgressPercent(0.0);
            sectionProgress.setUpdatedAt(LocalDateTime.now());
            return;
        }

        double perTagWeight = 100.0 / totalTags;
        double totalPercent = sectionProgress.getTagProgressList().stream()
                .mapToDouble(tag -> {
                    double completed = tag.getCompletedValue();
                    double required = tag.getRequiredValue();
                    return Math.min(completed / required, 1.0) * perTagWeight;
                }).sum();

        sectionProgress.setTotalProgressPercent(Math.min(totalPercent, 100.0));
        sectionProgress.setUpdatedAt(LocalDateTime.now());
    }

    private void recalculatePackageProgress(CareerPackage careerPackage) {
        int totalSections = careerPackage.getSectionProgressList().size();
        if (totalSections == 0) {
            careerPackage.getProgress().setTotalProgressPercent(0.0);
            return;
        }

        double perSectionWeight = 100.0 / totalSections;
        double totalPercent = careerPackage.getSectionProgressList().stream()
                .mapToDouble(section -> section.getTotalProgressPercent() * perSectionWeight / 100.0)
                .sum();

        careerPackage.getProgress().setTotalProgressPercent(Math.min(totalPercent, 100.0));
        careerPackage.getProgress().setUpdatedAt(LocalDateTime.now());
        careerPackage.setUpdatedAt(LocalDateTime.now());

        if (totalPercent == 0.0) {
            careerPackage.setStatus(CareerPackageStatus.NOT_STARTED);
        } else if (totalPercent >= 100.0) {
            careerPackage.setStatus(CareerPackageStatus.COMPLETED);
        } else {
            careerPackage.setStatus(CareerPackageStatus.IN_PROGRESS);
        }
    }
}
