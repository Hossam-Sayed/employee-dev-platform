package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerPackageRepository extends JpaRepository<CareerPackage, Long> {

    @EntityGraph(attributePaths = {
            "template",
            "sectionProgressList.packageTemplateSection.section",
            "sectionProgressList.tagProgressList.templateSectionRequiredTag.tag",
            "progress",
            "submissions"
    })
    Optional<CareerPackage> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndActiveTrue(Long userId);

    List<CareerPackage> findByUserIdInAndActiveTrue(List<Long> userIds);

}
