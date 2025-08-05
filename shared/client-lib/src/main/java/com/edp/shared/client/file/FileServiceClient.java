package com.edp.shared.client.file;

import com.edp.shared.client.file.model.FileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${file.service.url}")
public interface FileServiceClient {

    @PostMapping(value = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FileResponseDto> uploadFile(@RequestPart("file") MultipartFile file, @RequestHeader("Authorization") String token);
}
