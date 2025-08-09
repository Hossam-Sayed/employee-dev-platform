package com.edp.careerpackage.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "career_package_section_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageSectionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "career_package_id", nullable = false)
    private CareerPackage careerPackage;

    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "section_description")
    private String sectionDescription;

    @Column(name = "source_section_id")
    private Long sourceSectionId;

    @OneToMany(mappedBy = "careerPackageSectionProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<CareerPackageTagProgress> tagProgressList;

    @Column(name = "total_progress_percent")
    private Double totalProgressPercent;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

