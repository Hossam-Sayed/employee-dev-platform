package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageTemplateRepository extends JpaRepository<PackageTemplate, Long> {

    Page<PackageTemplate> findByDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(
            String department, String position, Pageable pageable
    );

    boolean existsByDepartmentIgnoreCaseAndPositionIgnoreCase(String department, String position);

    @EntityGraph(attributePaths = {
            "sections",
            "sections.section",
            "sections.requiredTags",
            "sections.requiredTags.tag"
    })
    Optional<PackageTemplate> findById(Long id);
}