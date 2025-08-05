package com.edp.filemanagement.data.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "fileDocuments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDocument {

    @Id
    private String id;

    private String filename;

    private String contentType;

    private long size;

    private String uploadedBy;

    private Instant uploadedAt;

    private String gridFsFileId;
}
