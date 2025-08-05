package com.edp.filemanagement.service;

import com.edp.filemanagement.client.AuthServiceClient;
import com.edp.filemanagement.client.model.UserProfileDto;
import com.edp.filemanagement.data.document.FileDocument;
import com.edp.filemanagement.data.repository.FileDocumentRepository;
import com.edp.filemanagement.mapper.FileMapper;
import com.edp.filemanagement.model.FileResponseDto;
import com.edp.filemanagement.security.jwt.JwtUserContext;
import com.mongodb.client.gridfs.model.GridFSFile;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;
    private final FileDocumentRepository fileDocumentRepository;
    private final FileMapper fileMapper;
    private final AuthServiceClient authServiceClient;

    public FileResponseDto storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        ObjectId gridFsFileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );

        Long uploadedBy = JwtUserContext.getUserId();

        FileDocument metadata = FileDocument.builder()
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .gridFsFileId(gridFsFileId.toHexString())
                .build();

        FileDocument saved = fileDocumentRepository.save(metadata);
        return fileMapper.toFileResponse(saved);
    }

    public GridFsResource retrieveFile(String fileId) throws IOException {
        Long currentUserId = JwtUserContext.getUserId();

        FileDocument metadata = fileDocumentRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found with ID: " + fileId));

        boolean isOwner = metadata.getUploadedBy().equals(currentUserId);
        boolean isManager = validateFileBelongsToManagedUser(metadata);
        if (!isOwner && !isManager) {
            throw new SecurityException("You are not authorized to access this file.");
        }

        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(metadata.getGridFsFileId()))));

        return gridFsOperations.getResource(gridFSFile);
    }

    private boolean validateFileBelongsToManagedUser(FileDocument metadata) {
        Long managerId = JwtUserContext.getUserId();
        String token = JwtUserContext.getToken();

        List<UserProfileDto> managedUsers;
        try {
            managedUsers = authServiceClient.getManagedUsers(managerId, token);
        } catch (FeignException ex) {
            throw new IllegalStateException("Failed to contact AuthService: " + ex.getMessage());
        }

        List<Long> userIds = managedUsers.stream()
                .map(UserProfileDto::getId)
                .toList();

        Long fileOwnerId = metadata.getUploadedBy();
        return userIds.contains(fileOwnerId);
    }


}
