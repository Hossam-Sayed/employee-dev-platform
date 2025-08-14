package com.edp.library.mapper;

import com.edp.library.data.entity.tag.TagRequest;
import com.edp.library.data.enums.TagRequestStatus;
import com.edp.library.model.enums.TagRequestStatusDTO;
import com.edp.library.model.tag.TagCreateRequestDTO;
import com.edp.library.model.tag.TagRequestResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reviewerComment", ignore = true)
    @Mapping(target = "reviewerId", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "requesterId", source = "requesterId")
    TagRequest toTagRequest(TagCreateRequestDTO dto, Long requesterId);

    TagRequestResponseDTO toTagRequestResponseDTO(TagRequest tagRequest);

    List<TagRequestResponseDTO> toTagRequestResponseDTOs(List<TagRequest> tagRequests);

    // This method is for mapping the enum from entity to DTO (and vice versa if needed)
    @Named("toTagRequestStatusDTO")
    TagRequestStatusDTO toTagRequestStatusDTO(TagRequestStatus status);

    @Named("toTagRequestStatusEntity")
    TagRequestStatus toTagRequestStatusEntity(TagRequestStatusDTO status);
}