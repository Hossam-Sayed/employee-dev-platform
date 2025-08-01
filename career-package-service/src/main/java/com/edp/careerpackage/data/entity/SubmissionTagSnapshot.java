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

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType;

    @Column(name = "required_value", nullable = false)
    private Double requiredValue;

    @Column(name = "submitted_value", nullable = false)
    private Double submittedValue;

    @Column(name = "proof_link", length = 1000)
    private String proofLink;
}
