package com.edp.notification.service;

import com.edp.notification.model.NotificationSubmissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for the Notification Service.
 * Defines the business logic for managing user notifications.
 */
public interface NotificationService {

    /**
     * Retrieves all notifications for the currently authenticated user with pagination and optional filtering.
     * The user ID is extracted from the JWT token.
     *
     * @param read     Optional boolean to filter by read status (true for read, false for unread).
     * @param pageable Pagination information (page number, size, sort).
     * @return A Page of notifications as NotificationSubmissionDTOs.
     */
    Page<NotificationSubmissionDTO> getMyNotifications(Boolean read, Pageable pageable);

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId The unique ID of the notification to update.
     * @return The updated NotificationSubmissionDTO.
     */
    NotificationSubmissionDTO markNotificationAsRead(String notificationId);
}