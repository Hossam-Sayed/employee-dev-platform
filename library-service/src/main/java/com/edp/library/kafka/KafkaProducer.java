package com.edp.library.kafka;

import com.edp.shared.client.NotificationDetails;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationDetails> kafkaTemplate;

    public void sendNotification(String topic, NotificationDetails notificationDetails) {
        kafkaTemplate.send(topic, notificationDetails.getOwnerId().toString(), notificationDetails);
    }
}