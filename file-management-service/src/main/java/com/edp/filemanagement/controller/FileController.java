package com.edp.filemanagement.controller;

import com.edp.filemanagement.controller.FileControllerApi;
import com.edp.filemanagement.data.document.FileDocument;
import com.edp.filemanagement.model.FileResponseDto;
import com.edp.filemanagement.security.jwt.JwtUserContext;
import com.edp.filemanagement.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController implements FileControllerApi {

    private final FileStorageService fileStorageService;

    @Override
    public ResponseEntity<FileResponseDto> uploadFile(MultipartFile file) throws IOException {

            FileResponseDto savedFile = fileStorageService.storeFile(file);
            return ResponseEntity.ok(savedFile);

    }

    @Override
    public ResponseEntity<Resource> getFile(String id) throws IOException {
        var fileResource = fileStorageService.retrieveFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
