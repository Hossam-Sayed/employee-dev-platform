package com.edp.shared.kafka.producer;

import com.edp.shared.kafka.model.LearningProgress;
import com.edp.shared.kafka.model.NotificationDetails;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationDetails> kafkaTemplate;
    private final KafkaTemplate<String, LearningProgress> learningProgressKafkaTemplate;

    public void sendNotification(NotificationDetails notificationDetails) {
        kafkaTemplate.send("notifications-topic", notificationDetails.getOwnerId().toString(), notificationDetails);
    }
    public void sendLearningProgress(LearningProgress progress) {

        learningProgressKafkaTemplate.send("learning-progress-topic", progress.getUserID().toString(), progress);
    }
}