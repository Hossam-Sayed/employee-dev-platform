package com.edp.library.data.entity.learning;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "learning_submission_tag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"learning_submission_id", "tag_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningSubmissionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_submission_id", nullable = false)
    private LearningSubmission learningSubmission;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
