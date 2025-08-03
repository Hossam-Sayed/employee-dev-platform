package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerPackageTagProgressRepository extends JpaRepository<CareerPackageTagProgress, Long> {

    @EntityGraph(attributePaths = {
            "careerPackageSectionProgress",
            "careerPackageSectionProgress.tagProgressList",
            "careerPackageSectionProgress.careerPackage",
            "careerPackageSectionProgress.careerPackage.sectionProgressList"
    })
    Optional<CareerPackageTagProgress> findById(Long id);

}
