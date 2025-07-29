package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageTemplateSectionRepository extends JpaRepository<PackageTemplateSection, Long> {

    boolean existsByTemplateAndSection(PackageTemplate template, Section section);
}