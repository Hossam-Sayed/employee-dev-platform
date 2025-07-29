package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerPackageTagProgressRepository extends JpaRepository<CareerPackageTagProgress, Long> {
}
