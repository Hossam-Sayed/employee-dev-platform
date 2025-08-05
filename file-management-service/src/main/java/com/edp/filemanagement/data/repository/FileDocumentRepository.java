package com.edp.filemanagement.data.repository;

import com.edp.filemanagement.data.document.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {
}
