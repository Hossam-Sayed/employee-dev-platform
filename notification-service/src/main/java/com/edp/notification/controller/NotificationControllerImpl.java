package com.edp.notification.controller;

import com.edp.notification.model.NotificationSubmissionDTO;
import com.edp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Page<NotificationSubmissionDTO>> getMyNotifications(
            Boolean read,
            Pageable pageable
    ) {
        Page<NotificationSubmissionDTO> notifications = notificationService.getMyNotifications(read, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<NotificationSubmissionDTO> markNotificationAsRead(String notificationId) {
        NotificationSubmissionDTO updatedNotification = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(updatedNotification);
    }
}