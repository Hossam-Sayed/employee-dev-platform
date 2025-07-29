package com.edp.careerpackage.data.entity;

import com.edp.careerpackage.data.enums.CareerPackageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_package_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackageProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "career_package_id", nullable = false, unique = true)
    private CareerPackage careerPackage;

    @Column(name = "total_progress_percent", nullable = false)
    private Double totalProgressPercent;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
