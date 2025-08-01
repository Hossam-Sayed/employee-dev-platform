package com.edp.careerpackage.data.entity;

import com.edp.careerpackage.data.enums.CareerPackageStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "career_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private PackageTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareerPackageStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "careerPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerPackageSectionProgress> sectionProgressList;

    @OneToOne(mappedBy = "careerPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private CareerPackageProgress progress;

    @OneToMany(mappedBy = "careerPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions;

    @Column(name = "active", nullable = false)
    private boolean active;

}
