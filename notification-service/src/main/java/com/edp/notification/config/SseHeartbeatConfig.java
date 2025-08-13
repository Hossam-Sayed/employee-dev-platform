package com.edp.notification.config;

import com.edp.notification.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SseHeartbeatConfig {
    private final SseEmitterService emitterService;

    @Scheduled(fixedRate = 15000)
    public void heartbeat() {
        emitterService.sendHeartbeat();
    }
}
