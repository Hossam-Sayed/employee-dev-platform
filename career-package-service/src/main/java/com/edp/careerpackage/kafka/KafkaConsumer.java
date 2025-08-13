package com.edp.careerpackage.kafka;

import com.edp.shared.kafka.model.LearningProgress;
import com.edp.careerpackage.service.TagProgressServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class KafkaConsumer {

    private final TagProgressServiceImpl tagProgressService;

    @KafkaListener(topics = "learning-progress-topic")
    public void receiveLearningProgress(@Payload LearningProgress progress) {
        try {
            log.info("Received learning progress event from Kafka: {}", progress);
            tagProgressService.updateLearningProgressFromKafka(progress);
            log.info("Successfully processed learning progress update.");
        } catch (Exception e) {
            log.error("Error processing learning progress event: {}", e.getMessage());
        }
    }
}