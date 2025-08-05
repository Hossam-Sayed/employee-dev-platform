package com.edp.filemanagement.controller;

import com.edp.filemanagement.model.FileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "File Management", description = "Manage upload, storage and download of files")
@RequestMapping("/api/files")
public interface FileControllerApi {

    @Operation(summary = "Upload a file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FileResponseDto> uploadFile(@RequestPart("file") MultipartFile file) throws IOException;

    @Operation(summary = "Download a file by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
    })
    @GetMapping("/{id}")
    ResponseEntity<Resource> getFile(@PathVariable String id) throws IOException;


    @Operation(summary = "Delete a file by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteFile(@PathVariable String id) throws IOException;

}

