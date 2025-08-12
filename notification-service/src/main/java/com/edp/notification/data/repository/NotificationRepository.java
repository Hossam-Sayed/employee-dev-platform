package com.edp.notification.data.repository;

import com.edp.notification.data.document.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Finds notifications for a specific owner, with optional filtering for 'read' status.
     * The results are returned as a paginated list.
     *
     * @param ownerId  The ID of the user who owns the notifications.
     * @param read     Optional boolean to filter by read status (e.g., true, false).
     * @param pageable Pagination information (page number, size, sort).
     * @return A Page of Notification documents.
     */
    Page<Notification> findByOwnerIdAndRead(Long ownerId, boolean read, Pageable pageable);

    /**
     * Finds all notifications for a specific owner, without filtering by 'read' status.
     * The results are returned as a paginated list.
     *
     * @param ownerId  The ID of the user who owns the notifications.
     * @param pageable Pagination information (page number, size, sort).
     * @return A Page of Notification documents.
     */
    Page<Notification> findByOwnerId(Long ownerId, Pageable pageable);
}