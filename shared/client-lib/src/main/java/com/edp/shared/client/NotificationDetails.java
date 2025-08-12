package com.edp.shared.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDetails {
    private Long submissionId;
    private SubmissionType submissionType;
    private String title;
    private Long ownerId;
    private Long actorId;
    private SubmissionStatus status;
    private Instant createdAt = Instant.now();
}