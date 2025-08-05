package com.edp.library.mapper;

import com.edp.library.data.entity.learning.Learning;
import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.entity.learning.LearningSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.learning.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

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
    @Mapping(target = "reviewerId", source = "reviewerId")
    LearningSubmission toLearningSubmission(LearningCreateRequestDTO dto, Learning learning, Long submitterId, Long reviewerId);

    // Map to LearningResponseDTO from Learning entity, fetching details from currentSubmission
    @Mapping(target = "currentSubmissionId", source = "currentSubmission.id")
    @Mapping(target = "title", source = "currentSubmission.title")
    @Mapping(target = "proofUrl", source = "currentSubmission.proofUrl")
    @Mapping(target = "status", source = "currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "currentSubmission.tags")
    LearningResponseDTO toLearningResponseDTO(Learning learning);

    List<LearningResponseDTO> toLearningResponseDTOs(List<Learning> learnings);

    @Mapping(target = "tagId", source = "tag.id")
    @Mapping(target = "tagName", source = "tag.name")
    LearningTagResponseDTO toLearningTagResponseDTO(LearningSubmissionTag learningSubmissionTag);

    List<LearningTagResponseDTO> toLearningTagResponseDTOs(Set<LearningSubmissionTag> tags);

    @Mapping(target = "learningId", source = "learning.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "tags")
    LearningSubmissionResponseDTO toLearningSubmissionResponseDTO(LearningSubmission submission);

    List<LearningSubmissionResponseDTO> toLearningSubmissionResponseDTOs(List<LearningSubmission> submissions);

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);

    // Map a single LearningTagDTO to LearningSubmissionTag (tag will be null initially)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "learningSubmission", expression = "java(submission)")
    LearningSubmissionTag toLearningSubmissionTag(LearningTagDTO dto, @Context LearningSubmission submission);

    // TODO: Not used, remove for looping only once using the above one
    // Map a list of LearningTagDTOs to a Set of LearningSubmissionTag entities
    // MapStruct will use the `toLearningSubmissionTag` method for each item
    Set<LearningSubmissionTag> toLearningSubmissionTags(List<LearningTagDTO> dtos, @Context LearningSubmission submission);
}