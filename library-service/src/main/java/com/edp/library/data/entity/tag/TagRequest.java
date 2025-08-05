package com.edp.library.data.entity.tag;

import com.edp.library.data.enums.TagRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Check(constraints = "(status != 'REJECTED' OR (reviewer_comment IS NOT NULL AND LENGTH(TRIM(reviewer_comment)) >= 10))")
@Table(name = "tag_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_name", nullable = false)
    private String requestedName;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TagRequestStatus status;

    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    // Any ADMIN can review requested tags,
    // the reviewer ID gets assigned after reviewing (nullable)
    @Column(name = "reviewer_id")
    private Long reviewerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}
