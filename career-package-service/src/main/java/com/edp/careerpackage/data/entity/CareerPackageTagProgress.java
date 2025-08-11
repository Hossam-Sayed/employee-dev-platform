package com.edp.careerpackage.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "career_package_tag_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageTagProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "career_package_section_progress_id", nullable = false)
    private CareerPackageSectionProgress careerPackageSectionProgress;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType;

    @Column(name = "required_value", nullable = false)
    private Double requiredValue;

    @Column(name = "source_required_tag_id")
    private Long sourceRequiredTagId;

    @Column(name = "completed_value", nullable = false)
    private Double completedValue;

    @Column(name = "proof_url", length = 1000)
    private String proofUrl;

    @Column(name = "file_id")
    private String fileId;

}
