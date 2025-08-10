package com.edp.tag.data.repository;

import com.edp.tag.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    boolean existsByNameIgnoreCase(String name);
}
