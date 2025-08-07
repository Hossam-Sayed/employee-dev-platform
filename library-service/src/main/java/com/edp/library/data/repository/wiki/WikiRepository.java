package com.edp.library.data.repository.wiki;

import com.edp.library.data.entity.wiki.Wiki;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiRepository extends JpaRepository<Wiki, Long> {

    Page<Wiki> findAll(Specification<Wiki> spec, Pageable pageable);
}