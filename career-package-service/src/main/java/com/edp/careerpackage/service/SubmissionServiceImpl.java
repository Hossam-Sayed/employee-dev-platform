package com.edp.careerpackage.service;

import com.edp.careerpackage.data.repository.SectionRepository;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionSectionSnapshotResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionSnapshotResponseDto;
import com.edp.careerpackage.model.submissionsnapshot.SubmissionTagSnapshotResponseDto;
import com.edp.shared.client.auth.AuthServiceClient;
import com.edp.shared.client.auth.model.UserProfileDto;
import com.edp.careerpackage.data.entity.*;
import com.edp.careerpackage.data.enums.CareerPackageStatus;
import com.edp.careerpackage.data.enums.SubmissionStatus;
import com.edp.careerpackage.data.repository.CareerPackageRepository;
import com.edp.careerpackage.data.repository.SubmissionRepository;
import com.edp.careerpackage.data.repository.SubmissionTagSnapshotRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.submission.CommentRequestDto;
import com.edp.careerpackage.model.submission.SubmissionResponseDto;
import com.edp.shared.client.tag.TagServiceClient;
import com.edp.shared.client.tag.model.TagResponseDto;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final CareerPackageRepository careerPackageRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagSnapshotRepository snapshotRepository;
    private final SectionRepository sectionRepository;
    private final SubmissionTagSnapshotRepository submissionTagSnapshotRepository;
    private final CareerPackageMapper mapper;
    private final AuthServiceClient authServiceClient;
    private final TagServiceClient tagServiceClient;


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
                                .tagId(tagProgress.getTagId())
                                .sourceSectionId(section.getSourceSection().getId())
                                .criteriaType(tagProgress.getCriteriaType())
                                .requiredValue(tagProgress.getRequiredValue())
                                .submittedValue(tagProgress.getCompletedValue())
                                .proofLink(tagProgress.getProofUrl())
                                .fileId(tagProgress.getFileId())
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


    @Override
    public SubmissionSnapshotResponseDto getSubmissionDetails(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with ID: " + submissionId));

        CareerPackage careerPackage = submission.getCareerPackage();
        Long submissionOwnerId = careerPackage.getUserId();
        String department = careerPackage.getDepartment();
        String position = careerPackage.getPosition();

        Long currentUserId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();

        boolean isOwner = currentUserId.equals(submissionOwnerId);
        if (!isOwner && !validateSubmissionBelongsToManagedUser(submission)) {
            throw new AuthenticationException("You are not authorized to view this submission.") {
            };
        }

        UserProfileDto user;
        try {
            user = authServiceClient.getUserById(submissionOwnerId, token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact AuthService: " + ex.getMessage());
        }

        List<SubmissionTagSnapshot> snapshots = submissionTagSnapshotRepository.findBySubmission(submission);

        Map<Long, List<SubmissionTagSnapshot>> snapshotsBySection = snapshots.stream()
                .collect(Collectors.groupingBy(SubmissionTagSnapshot::getSourceSectionId));

        List<Long> allTagIds = snapshots.stream().map(SubmissionTagSnapshot::getTagId).distinct().toList();
        List<Long> allSectionIds = snapshots.stream().map(SubmissionTagSnapshot::getSourceSectionId).distinct().toList();

        Map<Long, TagResponseDto> tagsMap;
        Map<Long, Section> sectionsMap;
        try {
            List<TagResponseDto> tagsList = tagServiceClient.findAllTagsByIds(allTagIds, token);
            tagsMap = tagsList.stream()
                    .collect(Collectors.toMap(TagResponseDto::getId, tag -> tag));
            List<Section> sectionsList = sectionRepository.findAllById(allSectionIds);
            sectionsMap = sectionsList.stream()
                    .collect(Collectors.toMap(Section::getId, section -> section));
            System.out.println(token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact the TagService: " + ex.getMessage());
        }


        List<SubmissionSectionSnapshotResponseDto> sectionDtos = snapshotsBySection.entrySet().stream()
                .map(entry -> {
                    Long sectionId = entry.getKey();
                    List<SubmissionTagSnapshot> sectionSnapshots = entry.getValue();

                    Section section = sectionsMap.get(sectionId);

                    List<SubmissionTagSnapshotResponseDto> tagDtos = sectionSnapshots.stream()
                            .map(snapshot -> {
                                TagResponseDto tag = tagsMap.get(snapshot.getTagId());
                                return SubmissionTagSnapshotResponseDto.builder()
                                        .tagName(tag.getName())
                                        .criteriaType(snapshot.getCriteriaType())
                                        .requiredValue(snapshot.getRequiredValue())
                                        .submittedValue(snapshot.getSubmittedValue())
                                        .proofLink(snapshot.getProofLink())
                                        .fileId(snapshot.getFileId())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return SubmissionSectionSnapshotResponseDto.builder()
                            .sectionId(sectionId)
                            .sectionName(section.getName())
                            .sectionDescription(section.getDescription())
                            .tags(tagDtos)
                            .build();
                })
                .collect(Collectors.toList());

        return SubmissionSnapshotResponseDto.builder()
                .submissionId(submission.getId())
                .user(user)
                .department(department)
                .position(position)
                .status(submission.getStatus().toString())
                .comment(submission.getComment())
                .submittedAt(submission.getSubmittedAt())
                .reviewedAt(submission.getReviewedAt())
                .sections(sectionDtos)
                .build();
    }

}
