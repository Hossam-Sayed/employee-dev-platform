package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.Learning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

    Page<Learning> findAll (Specification<Learning> spec, Pageable pageable);

    List<Learning> findByEmployeeId(Long employeeId);

    // TODO: Remove?
    Optional<Learning> findByIdAndEmployeeId(Long id, Long employeeId);
}