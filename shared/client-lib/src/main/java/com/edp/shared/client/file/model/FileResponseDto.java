package com.edp.shared.client.file.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileResponseDto {
    private String id;

    private String filename;

    private String contentType;

    private long size;

    private Long uploadedBy;

    private LocalDateTime uploadedAt;
}
