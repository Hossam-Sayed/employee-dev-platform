package com.edp.library.data.entity.wiki;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "wiki_submission_tag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wiki_submission_id", "tag_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiSubmissionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wiki_submission_id", nullable = false)
    private WikiSubmission wikiSubmission;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
