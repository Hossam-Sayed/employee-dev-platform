package com.edp.library.mapper;

import com.edp.library.data.entity.blog.Blog;
import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.entity.blog.BlogSubmissionTag;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.model.blog.BlogCreateRequestDTO;
import com.edp.library.model.blog.BlogResponseDTO;
import com.edp.library.model.blog.BlogSubmissionResponseDTO;
import com.edp.library.model.blog.BlogTagResponseDTO;
import com.edp.library.model.enums.SubmissionStatusDTO;
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
public interface BlogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentSubmission", ignore = true)
    @Mapping(target = "authorId", source = "authorId")
    Blog toBlog(Long authorId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blog", source = "blog")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reviewerComment", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "submitterId", source = "submitterId")
    @Mapping(target = "reviewerId", ignore = true)
    @Mapping(target = "documentId", source = "documentId")
    BlogSubmission toBlogSubmission(BlogCreateRequestDTO dto, Blog blog, Long submitterId, String documentId);

    @Mapping(target = "currentSubmissionId", source = "blog.currentSubmission.id")
    @Mapping(target = "title", source = "blog.currentSubmission.title")
    @Mapping(target = "description", source = "blog.currentSubmission.description")
    @Mapping(target = "documentId", source = "blog.currentSubmission.documentId")
    @Mapping(target = "status", source = "blog.currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(blog.getCurrentSubmission().getTags(), tagResponseDtos))"
    )
    BlogResponseDTO toBlogResponseDTO(Blog blog, List<TagResponseDto> tagResponseDtos);

    @Mapping(target = "blogId", source = "submission.blog.id")
    @Mapping(target = "status", source = "submission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(
            target = "tags",
            expression = "java(mapTags(submission.getTags(), tagResponseDtos))"
    )
    @Mapping(target = "documentId", source = "submission.documentId")
    BlogSubmissionResponseDTO toBlogSubmissionResponseDTO(BlogSubmission submission, List<TagResponseDto> tagResponseDtos);

    @Named("mapTags")
    default List<BlogTagResponseDTO> mapTags(Set<BlogSubmissionTag> submissionTags, List<TagResponseDto> tagResponseDtos) {
        if (submissionTags == null || submissionTags.isEmpty() || tagResponseDtos == null || tagResponseDtos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> tagNameMap = tagResponseDtos.stream()
                .collect(Collectors.toMap(TagResponseDto::getId, TagResponseDto::getName));

        return submissionTags.stream()
                .map(submissionTag -> BlogTagResponseDTO.builder()
                        .id(submissionTag.getId())
                        .tagId(submissionTag.getTagId())
                        .tagName(tagNameMap.getOrDefault(submissionTag.getTagId(), "Unknown Tag"))
                        .createdAt(submissionTag.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    default List<BlogResponseDTO> toBlogResponseDTOs(List<Blog> blogs, List<TagResponseDto> tagResponseDtos) {
        if (blogs == null || blogs.isEmpty()) {
            return Collections.emptyList();
        }
        return blogs.stream()
                .map(blog -> toBlogResponseDTO(blog, tagResponseDtos))
                .collect(Collectors.toList());
    }

    default List<BlogSubmissionResponseDTO> toBlogSubmissionResponseDTOs(List<BlogSubmission> submissions, List<TagResponseDto> tagResponseDtos) {
        if (submissions == null || submissions.isEmpty()) {
            return Collections.emptyList();
        }
        return submissions.stream()
                .map(submission -> toBlogSubmissionResponseDTO(submission, tagResponseDtos))
                .collect(Collectors.toList());
    }

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);
}