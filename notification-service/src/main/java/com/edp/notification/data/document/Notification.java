package com.edp.notification.data.document;

import com.edp.shared.client.SubmissionStatus;
import com.edp.notification.data.enums.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A general-purpose MongoDB document to store notification information
 * for various types of material submissions. Now includes a 'read' field.
 */
@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    // MongoDB's default primary key
    @Id
    private String id;

    // The ID of the original submission entity (Learning, Blog, or Wiki).
    private Long submissionId;

    // The type of the submission, used to differentiate between materials.
    private SubmissionType submissionType;

    // The title of the submission.
    private String title;

    // The ID of the user who owns this notification.
    private Long ownerId;

    // The ID of the user who performed the action (made/reviewed the submission).
    private Long actorId;

    // The new status of the submission.
    private SubmissionStatus status;

    // A flag to track if the notification has been read by the owner.
    private boolean read = false;

    // The timestamp when the notification was created.
    @CreationTimestamp
    private Instant createdAt;
}