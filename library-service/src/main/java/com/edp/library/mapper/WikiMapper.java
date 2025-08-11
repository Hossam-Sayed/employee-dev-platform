package com.edp.library.mapper;

import com.edp.library.data.entity.wiki.Wiki;
import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.entity.wiki.WikiSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.model.enums.SubmissionStatusDTO;
import com.edp.library.model.wiki.WikiCreateRequestDTO;
import com.edp.library.model.wiki.WikiResponseDTO;
import com.edp.library.model.wiki.WikiSubmissionResponseDTO;
import com.edp.library.model.wiki.WikiTagResponseDTO;
import com.edp.shared.client.tag.model.TagResponseDto;
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
public interface WikiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentSubmission", ignore = true)
    @Mapping(target = "authorId", source = "authorId")
    Wiki toWiki(Long authorId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wiki", source = "wiki")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reviewerComment", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "submitterId", source = "submitterId")
    @Mapping(target = "reviewerId", ignore = true)
    @Mapping(target = "documentId", source = "documentId")
    WikiSubmission toWikiSubmission(WikiCreateRequestDTO dto, Wiki wiki, Long submitterId, String documentId);

    @Mapping(target = "currentSubmissionId", source = "wiki.currentSubmission.id")
    @Mapping(target = "title", source = "wiki.currentSubmission.title")
    @Mapping(target = "description", source = "wiki.currentSubmission.description")
    @Mapping(target = "documentId", source = "wiki.currentSubmission.documentId")
    @Mapping(target = "status", source = "wiki.currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(wiki.getCurrentSubmission().getTags(), tagResponseDtos))"
    )
    WikiResponseDTO toWikiResponseDTO(Wiki wiki, List<TagResponseDto> tagResponseDtos);

    @Mapping(target = "wikiId", source = "submission.wiki.id")
    @Mapping(target = "status", source = "submission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(submission.getTags(), tagResponseDtos))"
    )
    @Mapping(target = "documentId", source = "submission.documentId")
    WikiSubmissionResponseDTO toWikiSubmissionResponseDTO(WikiSubmission submission, List<TagResponseDto> tagResponseDtos);

    @Named("mapTags")
    default List<WikiTagResponseDTO> mapTags(Set<WikiSubmissionTag> submissionTags, List<TagResponseDto> tagResponseDtos) {
        if (submissionTags == null || submissionTags.isEmpty() || tagResponseDtos == null || tagResponseDtos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> tagNameMap = tagResponseDtos.stream()
                .collect(Collectors.toMap(TagResponseDto::getId, TagResponseDto::getName));

        return submissionTags.stream()
                .map(submissionTag -> WikiTagResponseDTO.builder()
                        .id(submissionTag.getId())
                        .tagId(submissionTag.getTagId())
                        .tagName(tagNameMap.getOrDefault(submissionTag.getTagId(), "Unknown Tag"))
                        .createdAt(submissionTag.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    default List<WikiResponseDTO> toWikiResponseDTOs(List<Wiki> wikis, List<TagResponseDto> tagResponseDtos) {
        if (wikis == null || wikis.isEmpty()) {
            return Collections.emptyList();
        }
        return wikis.stream()
                .map(wiki -> toWikiResponseDTO(wiki, tagResponseDtos))
                .collect(Collectors.toList());
    }

    default List<WikiSubmissionResponseDTO> toWikiSubmissionResponseDTOs(List<WikiSubmission> submissions, List<TagResponseDto> tagResponseDtos) {
        if (submissions == null || submissions.isEmpty()) {
            return Collections.emptyList();
        }
        return submissions.stream()
                .map(submission -> toWikiSubmissionResponseDTO(submission, tagResponseDtos))
                .collect(Collectors.toList());
    }

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);
}
