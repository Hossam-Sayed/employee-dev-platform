package com.edp.notification.sse;

import com.edp.notification.model.NotificationSubmissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterService {

    // Map<ownerId, emitter>
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long ownerId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        // Remove on completion/error/timeout
        emitter.onCompletion(() -> removeEmitter(ownerId));
        emitter.onTimeout(() -> removeEmitter(ownerId));
        emitter.onError(e -> removeEmitter(ownerId));

        emitters.put(ownerId, emitter);
        log.info("SSE emitter registered for user {}", ownerId);
        return emitter;
    }

    public void sendNotification(Long ownerId, NotificationSubmissionDTO notification) {
        SseEmitter emitter = emitters.get(ownerId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("notification")
                                .data(notification)
                                .reconnectTime(3000)
                );
                log.info("Sent notification to user {}", ownerId);
            } catch (IOException e) {
                log.warn("Emitter for user {} is dead, removing", ownerId);
                removeEmitter(ownerId);
            }
        } else {
            log.info("No active SSE connection for user {}", ownerId);
        }
    }

    public void sendHeartbeat() {
        emitters.forEach((ownerId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data("keepalive"));
            } catch (IOException e) {
                removeEmitter(ownerId);
            }
        });
    }

    private void removeEmitter(Long ownerId) {
        emitters.remove(ownerId);
        log.info("Removed SSE emitter for user {}", ownerId);
    }
}
