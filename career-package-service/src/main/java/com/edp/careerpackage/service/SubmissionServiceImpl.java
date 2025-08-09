package com.edp.careerpackage.service;

import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.shared.client.auth.model.UserProfileDto;
import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.data.enums.SubmissionStatus;
import com.edp.careerpackage.data.repository.CareerPackageRepository;
import com.edp.careerpackage.data.repository.SubmissionRepository;
import com.edp.careerpackage.data.repository.SubmissionTagSnapshotRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.mapper.SubmissionTagSnapshotMapper;
import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import com.edp.shared.security.jwt.JwtUserContext;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final CareerPackageRepository careerPackageRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagSnapshotRepository snapshotRepository;
    private final CareerPackageMapper mapper;
    private final SubmissionTagSnapshotMapper snapshotMapper;
    private final AuthServiceClient authServiceClient;

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

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionTagSnapshotResponseDto> getSnapshotsBySubmissionId(Long submissionId) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
        CareerPackage careerPackage = submission.getCareerPackage();

        Long userId = JwtUserContext.getUserId();
        boolean isOwner = careerPackage.getUserId().equals(userId);
        boolean isManager = validateSubmissionBelongsToManagedUser(submission);
        if (!isOwner && !isManager) {
            throw new AuthenticationException("You are not authorized to view this submission") {
            };
        }

        List<SubmissionTagSnapshot> snapshots = snapshotRepository.findBySubmission(submission);
        return snapshotMapper.toSnapshotResponseDtoList(snapshots);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionsByUserIds() {
        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();

        List<UserProfileDto> managedUsers;
        try {
            managedUsers = authServiceClient.getManagedUsers(managerId, token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact AuthService: " + ex.getMessage());
        }

        List<Long> userIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();
        List<CareerPackage> packages = careerPackageRepository.findByUserIdInAndActiveTrue(userIds);

        List<Submission> submissions = packages.stream()
                .flatMap(pkg -> pkg.getSubmissions().stream())
                .toList();

        return mapper.toSubmissionResponseDtoList(submissions);
    }


    @Override
    @Transactional
    public SubmissionResponseDto approveSubmission(Long submissionId, CommentRequestDto request) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        if (!validateSubmissionBelongsToManagedUser(submission)) {
            throw new AuthenticationException("You are not authorized to review this submission") {
            };
        }

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new DataIntegrityViolationException("Only pending submissions can be reviewed.");
        }

        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setComment(request.getComment());
        submission.setReviewedAt(LocalDateTime.now());
        submission.getCareerPackage().setStatus(CareerPackageStatus.APPROVED);

        submissionRepository.save(submission);
        return mapper.toSubmissionResponseDto(submission);
    }

    @Override
    @Transactional
    public SubmissionResponseDto rejectSubmission(Long submissionId, CommentRequestDto request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        if (!validateSubmissionBelongsToManagedUser(submission)) {
            throw new AuthenticationException("You are not authorized to review this submission") {
            };
        }

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new DataIntegrityViolationException("Only pending submissions can be reviewed.");
        }

        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setComment(request.getComment());
        submission.setReviewedAt(LocalDateTime.now());
        submission.getCareerPackage().setStatus(CareerPackageStatus.REJECTED);

        submissionRepository.save(submission);
        return mapper.toSubmissionResponseDto(submission);
    }


    private List<SubmissionTagSnapshot> buildSnapshots(CareerPackage careerPackage, Submission submission) {
        return careerPackage.getSectionProgressList().stream()
                .flatMap(section -> section.getTagProgressList().stream().map(tagProgress ->
                        SubmissionTagSnapshot.builder()
                                .submission(submission)
                                .tagName(tagProgress.getTagName())
                                .sectionName(section.getSectionName())
                                .criteriaType(tagProgress.getCriteriaType())
                                .requiredValue(tagProgress.getRequiredValue())
                                .submittedValue(tagProgress.getCompletedValue())
                                .proofLink(tagProgress.getProofUrl())
                                .build()
                ))
                .toList();
    }

    private boolean validateSubmissionBelongsToManagedUser(Submission submission) {
        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();

        List<UserProfileDto> managedUsers;
        try {
            managedUsers = authServiceClient.getManagedUsers(managerId, token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact AuthService: " + ex.getMessage());
        }

        List<Long> userIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Long submissionOwnerId = submission.getCareerPackage().getUserId();
        return userIds.contains(submissionOwnerId);
    }

}
