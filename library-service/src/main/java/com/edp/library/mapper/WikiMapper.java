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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

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

    @Mapping(target = "currentSubmissionId", source = "currentSubmission.id")
    @Mapping(target = "title", source = "currentSubmission.title")
    @Mapping(target = "description", source = "currentSubmission.description")
    @Mapping(target = "documentId", source = "currentSubmission.documentId")
    @Mapping(target = "status", source = "currentSubmission.status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "currentSubmission.tags")
    WikiResponseDTO toWikiResponseDTO(Wiki wiki);

    List<WikiResponseDTO> toWikiResponseDTOs(List<Wiki> wikis);

    @Mapping(target = "tagId", source = "tag.id")
    @Mapping(target = "tagName", source = "tag.name")
    WikiTagResponseDTO toWikiTagResponseDTO(WikiSubmissionTag wikiSubmissionTag);

    List<WikiTagResponseDTO> toWikiTagResponseDTOs(Set<WikiSubmissionTag> tags);

    @Mapping(target = "wikiId", source = "wiki.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "toSubmissionStatusDTO")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "documentId", source = "documentId")
    WikiSubmissionResponseDTO toWikiSubmissionResponseDTO(WikiSubmission submission);

    List<WikiSubmissionResponseDTO> toWikiSubmissionResponseDTOs(List<WikiSubmission> submissions);

    @Named("toSubmissionStatusDTO")
    SubmissionStatusDTO toSubmissionStatusDTO(SubmissionStatus status);

    @Named("toSubmissionStatusEntity")
    SubmissionStatus toSubmissionStatusEntity(SubmissionStatusDTO status);
}