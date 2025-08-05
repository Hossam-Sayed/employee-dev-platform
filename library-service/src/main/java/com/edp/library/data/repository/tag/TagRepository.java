package com.edp.library.data.repository.tag;

import com.edp.library.data.entity.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Find a tag by its name (for checking uniqueness before creating new tags)
    Optional<Tag> findByNameIgnoreCase(String name);

    List<Tag> findByNameContainingIgnoreCaseAndActive(String name, boolean active);

    Page<Tag> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Tag> findByActive(boolean active);

    Page<Tag> findByActive(boolean active, Pageable pageable);

}