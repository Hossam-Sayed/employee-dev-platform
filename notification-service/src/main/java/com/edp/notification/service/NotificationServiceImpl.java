package com.edp.notification.service;

import com.edp.notification.data.document.Notification;
import com.edp.notification.data.repository.NotificationRepository;
import com.edp.notification.exception.ResourceNotFoundException;
import com.edp.notification.mapper.NotificationMapper;
import com.edp.notification.model.NotificationSubmissionDTO;
import com.edp.shared.security.jwt.JwtUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Page<NotificationSubmissionDTO> getMyNotifications(Boolean read, Pageable pageable) {
        Long ownerId = JwtUserContext.getUserId();
        Page<Notification> notifications;
        if (read != null) {
            // Filter by read status if the parameter is provided
            notifications = notificationRepository.findByOwnerIdAndRead(ownerId, read, pageable);
        } else {
            // Otherwise, get all notifications for the user
            notifications = notificationRepository.findByOwnerId(ownerId, pageable);
        }
        return notifications.map(notificationMapper::toSubmissionNotificationDTO);
    }

    @Override
    public NotificationSubmissionDTO markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        // Check if the current user is the owner of the notification before updating.
        Long ownerId = JwtUserContext.getUserId();
        if (!ownerId.equals(notification.getOwnerId())) {
            throw new IllegalStateException("User is not authorized to mark this notification as read.");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toSubmissionNotificationDTO(updatedNotification);
    }
}