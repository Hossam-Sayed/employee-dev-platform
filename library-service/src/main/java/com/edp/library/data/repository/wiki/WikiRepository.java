package com.edp.library.data.repository.wiki;

import com.edp.library.data.entity.wiki.Wiki;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository extends JpaRepository<Wiki, Long> {

    List<Wiki> findByAuthorId(Long authorId);

    // TODO: Remove?
    Optional<Wiki> findByIdAndAuthorId(Long id, Long authorId);

    Page<Wiki> findAll(Specification<Wiki> spec, Pageable pageable);
}