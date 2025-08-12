package com.edp.notification.model;

import com.edp.notification.data.enums.SubmissionStatus;
import com.edp.notification.data.enums.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSubmissionDTO {

    private String id;
    private Long submissionId;
    private SubmissionType submissionType;
    private String title;
    private Long ownerId;
    private Long actorId;
    private SubmissionStatus status;
    private boolean read;
    private Instant createdAt;
}