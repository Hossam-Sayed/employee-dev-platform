package com.edp.library.data.entity.learning;

import com.edp.library.data.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Check(constraints = "(status != 'REJECTED' OR (reviewer_comment IS NOT NULL AND LENGTH(TRIM(reviewer_comment)) >= 10))")
@Check(constraints = "((status IN ('APPROVED', 'REJECTED') AND reviewed_at IS NOT NULL) OR (status = 'PENDING' AND reviewed_at IS NULL))")
@Table(name = "learning_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_id", nullable = false)
    private Learning learning;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "proof_url", nullable = false, columnDefinition = "TEXT")
    private String proofUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubmissionStatus status;

    @Column(name = "submitter_id", nullable = false)
    private Long submitterId;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    @OneToMany(mappedBy = "learningSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LearningSubmissionTag> tags = new HashSet<>();
}
