package com.edp.notification.kafka;

import com.edp.notification.data.document.Notification;
import com.edp.notification.data.repository.NotificationRepository;
import com.edp.notification.mapper.NotificationMapper;
import com.edp.notification.model.NotificationSubmissionDTO;
import com.edp.notification.sse.SseEmitterService;
import com.edp.shared.client.NotificationDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class KafkaConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SseEmitterService emitterService;

    @KafkaListener(topics = "notifications-topic")
    public void receiveNotification(@Payload NotificationDetails notificationDetails) {
        try {
            log.info("Received notification event from Kafka: {}", notificationDetails);

            // Map the DTO from Kafka to a Notification document for MongoDB
            Notification notification = notificationMapper.toNotification(notificationDetails);
            notification.setRead(false);

            // Save the new notification to the database
            notificationRepository.save(notification);

            log.info("Successfully created new notification in the database.");

            // Map to DTO
            NotificationSubmissionDTO dto = notificationMapper.toSubmissionNotificationDTO(notification);

            // Push via SSE
            emitterService.sendNotification(notification.getOwnerId(), dto);

            log.info("Successfully emitted the new notification to the channel.");
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage());
        }
    }
}