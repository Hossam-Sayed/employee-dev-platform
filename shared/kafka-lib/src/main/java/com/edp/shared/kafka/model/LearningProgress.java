package com.edp.shared.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgress {
    private Long userID;
    private Map<Long, Double> updates;
    private String proofUrl;
}