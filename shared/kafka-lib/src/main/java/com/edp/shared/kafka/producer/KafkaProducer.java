package com.edp.shared.kafka.producer;

import com.edp.shared.kafka.model.NotificationDetails;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationDetails> kafkaTemplate;

    public void sendNotification(NotificationDetails notificationDetails) {
        kafkaTemplate.send("notifications-topic", notificationDetails.getOwnerId().toString(), notificationDetails);
    }
}