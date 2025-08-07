package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.Learning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

    Page<Learning> findAll(Specification<Learning> spec, Pageable pageable);
}