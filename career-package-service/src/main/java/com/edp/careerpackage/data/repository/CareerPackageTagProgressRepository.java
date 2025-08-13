package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT cptp FROM CareerPackageTagProgress cptp " +
            "JOIN cptp.careerPackageSectionProgress cpsp " +
            "JOIN cpsp.careerPackage cp " +
            "JOIN cpsp.sourceSection s " +
            "WHERE cp.userId = :userId " +
            "AND cptp.tagId = :tagId " +
            "AND s.name = 'Learning' " +
            "AND cp.active = true")
    Optional<CareerPackageTagProgress> findByUserIdAndTagIdAndLearningSection(@Param("userId") Long userId, @Param("tagId") Long tagId);

}
