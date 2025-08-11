package com.edp.library.mapper;

import com.edp.library.data.entity.learning.Learning;
import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.entity.learning.LearningSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.learning.*;
import com.edp.shared.client.tag.model.TagResponseDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentSubmission", ignore = true)
    @Mapping(target = "employeeId", source = "employeeId")
    Learning toLearning(Long employeeId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "learning", source = "learning")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reviewerComment", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "tags", ignore = true) // Tags will be set separately in service after mapping
    @Mapping(target = "submitterId", source = "submitterId")
    @Mapping(target = "reviewerId", ignore = true)
    LearningSubmission toLearningSubmission(LearningCreateRequestDTO dto, Learning learning, Long submitterId);

    // Map to LearningResponseDTO from Learning entity, fetching details from currentSubmission
    @Mapping(target = "currentSubmissionId", source = "learning.currentSubmission.id")
    @Mapping(target = "title", source = "learning.currentSubmission.title")
    @Mapping(target = "proofUrl", source = "learning.currentSubmission.proofUrl")
    @Mapping(target = "status", source = "learning.currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(learning.getCurrentSubmission().getTags(), tagResponseDtos))"
    )
    LearningResponseDTO toLearningResponseDTO(Learning learning, List<TagResponseDto> tagResponseDtos);

    @Mapping(target = "learningId", source = "submission.learning.id")
    @Mapping(target = "status", source = "submission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(submission.getTags(), tagResponseDtos))"
    )
    LearningSubmissionResponseDTO toLearningSubmissionResponseDTO(LearningSubmission submission, List<TagResponseDto> tagResponseDtos);

    @Named("mapTags")
    default List<LearningTagResponseDTO> mapTags(Set<LearningSubmissionTag> submissionTags, List<TagResponseDto> tagResponseDtos) {
        if (submissionTags == null || submissionTags.isEmpty() || tagResponseDtos == null || tagResponseDtos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> tagNameMap = tagResponseDtos.stream()
                .collect(Collectors.toMap(TagResponseDto::getId, TagResponseDto::getName));

        return submissionTags.stream()
                .map(submissionTag -> LearningTagResponseDTO.builder()
                        .id(submissionTag.getId())
                        .tagId(submissionTag.getTagId())
                        .tagName(tagNameMap.getOrDefault(submissionTag.getTagId(), "Unknown Tag"))
                        .durationMinutes(submissionTag.getDurationMinutes())
                        .createdAt(submissionTag.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    default List<LearningResponseDTO> toLearningResponseDTOs(List<Learning> learnings, List<TagResponseDto> tagResponseDtos) {
        if (learnings == null || learnings.isEmpty()) {
            return Collections.emptyList();
        }
        return learnings.stream()
                .map(learning -> toLearningResponseDTO(learning, tagResponseDtos))
                .collect(Collectors.toList());
    }

    default List<LearningSubmissionResponseDTO> toLearningSubmissionResponseDTOs(List<LearningSubmission> submissions, List<TagResponseDto> tagResponseDtos) {
        if (submissions == null || submissions.isEmpty()) {
            return Collections.emptyList();
        }
        return submissions.stream()
                .map(submission -> toLearningSubmissionResponseDTO(submission, tagResponseDtos))
                .collect(Collectors.toList());
    }

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "learningSubmission", expression = "java(submission)")
    @Mapping(target = "tagId", source = "dto.tagId")
    @Mapping(target = "durationMinutes", source = "dto.durationMinutes")
    @Mapping(target = "createdAt", ignore = true)
    LearningSubmissionTag toLearningSubmissionTag(LearningTagDTO dto, @Context LearningSubmission submission);
}