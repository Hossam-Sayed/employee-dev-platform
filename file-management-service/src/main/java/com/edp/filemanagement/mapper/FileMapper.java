package com.edp.filemanagement.mapper;

import com.edp.filemanagement.data.document.FileDocument;
import com.edp.filemanagement.model.FileResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileResponseDto toFileResponse(FileDocument fileDocument);
}
