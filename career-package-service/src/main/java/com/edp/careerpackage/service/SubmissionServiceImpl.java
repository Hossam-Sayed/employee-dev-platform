package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.data.enums.SubmissionStatus;
import com.edp.careerpackage.data.repository.CareerPackageRepository;
import com.edp.careerpackage.data.repository.SubmissionRepository;
import com.edp.careerpackage.data.repository.SubmissionTagSnapshotRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.security.jwt.JwtUserContext;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final CareerPackageRepository careerPackageRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagSnapshotRepository snapshotRepository;
    private final CareerPackageMapper mapper;

    @Override
    @Transactional
    public SubmissionResponseDto submitCareerPackage() {
        Long userId = JwtUserContext.getUserId();

        CareerPackage careerPackage = careerPackageRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Career package not found"));

        if (careerPackage.getProgress().getTotalProgressPercent() < 100.0) {
            throw new DataIntegrityViolationException("Submission not allowed: package progress is not 100%");
        }

        if (!CareerPackageStatus.COMPLETED.equals(careerPackage.getStatus())) {
            throw new DataIntegrityViolationException("Submission not allowed: package status is not COMPLETED");
        }

        Submission submission = Submission.builder()
                .careerPackage(careerPackage)
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.PENDING)
                .build();

        Submission saved = submissionRepository.save(submission);

        List<SubmissionTagSnapshot> snapshots = buildSnapshots(careerPackage, saved);
        snapshotRepository.saveAll(snapshots);

        careerPackage.setStatus(CareerPackageStatus.SUBMITTED);
        careerPackage.setUpdatedAt(LocalDateTime.now());

        return mapper.toSubmissionResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionHistory() {
        Long userId = JwtUserContext.getUserId();

        CareerPackage careerPackage = careerPackageRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Career package not found"));

        return mapper.toSubmissionResponseDtoList(careerPackage.getSubmissions());
    }

    private List<SubmissionTagSnapshot> buildSnapshots(CareerPackage careerPackage, Submission submission) {
        return careerPackage.getSectionProgressList().stream()
                .flatMap(section -> section.getTagProgressList().stream().map(tagProgress ->
                        SubmissionTagSnapshot.builder()
                                .submission(submission)
                                .tagName(tagProgress.getTemplateSectionRequiredTag().getTag().getName())
                                .sectionName(section.getPackageTemplateSection().getSection().getName())
                                .criteriaType(tagProgress.getTemplateSectionRequiredTag().getCriteriaType().name())
                                .requiredValue(tagProgress.getTemplateSectionRequiredTag().getCriteriaMinValue())
                                .submittedValue(tagProgress.getCompletedValue())
                                .proofLink(tagProgress.getProofUrl())
                                .build()
                ))
                .toList();
    }
}
