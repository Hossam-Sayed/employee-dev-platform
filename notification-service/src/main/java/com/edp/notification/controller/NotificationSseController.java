package com.edp.notification.controller;

import com.edp.notification.sse.SseEmitterService;
import com.edp.shared.security.jwt.JwtUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class NotificationSseController {

    private final SseEmitterService emitterService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        Long ownerId = JwtUserContext.getUserId();
        return emitterService.createEmitter(ownerId);
    }
}
