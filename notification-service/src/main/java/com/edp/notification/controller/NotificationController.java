package com.edp.notification.controller;

import com.edp.notification.model.NotificationSubmissionDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/notifications")
public interface NotificationController {

    @GetMapping
    ResponseEntity<Page<NotificationSubmissionDTO>> getMyNotifications(
            @RequestParam(required = false) Boolean read,
            Pageable pageable
    );

    @PatchMapping("/{notificationId}/read")
    ResponseEntity<NotificationSubmissionDTO> markNotificationAsRead(
            @PathVariable("notificationId") String notificationId
    );
}