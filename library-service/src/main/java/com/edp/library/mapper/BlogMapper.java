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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

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

    @Mapping(target = "currentSubmissionId", source = "currentSubmission.id")
    @Mapping(target = "title", source = "currentSubmission.title")
    @Mapping(target = "description", source = "currentSubmission.description")
    @Mapping(target = "documentId", source = "currentSubmission.documentId")
    @Mapping(target = "status", source = "currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "currentSubmission.tags")
    BlogResponseDTO toBlogResponseDTO(Blog blog);

    List<BlogResponseDTO> toBlogResponseDTOs(List<Blog> blogs);

    @Mapping(target = "tagId", source = "tag.id")
    @Mapping(target = "tagName", source = "tag.name")
    BlogTagResponseDTO toBlogTagResponseDTO(BlogSubmissionTag blogSubmissionTag);

    List<BlogTagResponseDTO> toBlogTagResponseDTOs(Set<BlogSubmissionTag> tags);

    @Mapping(target = "blogId", source = "blog.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "documentId", source = "documentId")
    BlogSubmissionResponseDTO toBlogSubmissionResponseDTO(BlogSubmission submission);

    List<BlogSubmissionResponseDTO> toBlogSubmissionResponseDTOs(List<BlogSubmission> submissions);

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);
}