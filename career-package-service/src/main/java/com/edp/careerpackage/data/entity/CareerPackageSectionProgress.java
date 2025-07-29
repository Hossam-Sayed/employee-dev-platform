package com.edp.careerpackage.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "package_template_section_id", nullable = false)
    private PackageTemplateSection packageTemplateSection;

    @OneToMany(mappedBy = "careerPackageSectionProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerPackageTagProgress> tagProgressList;

    @Column(name = "total_progress_percent")
    private Double totalProgressPercent;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

