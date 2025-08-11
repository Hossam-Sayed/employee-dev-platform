package com.edp.careerpackage.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_tag_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionTagSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "source_section_id",nullable = false)
    private Long sourceSectionId;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType;

    @Column(name = "required_value", nullable = false)
    private Double requiredValue;

    @Column(name = "submitted_value", nullable = false)
    private Double submittedValue;

    @Column(name = "proof_link", length = 1000)
    private String proofLink;

    @Column(name = "file_id")
    private String fileId;

}
