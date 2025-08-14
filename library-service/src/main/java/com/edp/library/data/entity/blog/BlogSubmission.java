package com.edp.library.data.entity.blog;

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
@Table(name = "blog_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_id", nullable = false, columnDefinition = "TEXT")
    private String documentId;

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

    @OneToMany(mappedBy = "blogSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlogSubmissionTag> tags = new HashSet<>();
}