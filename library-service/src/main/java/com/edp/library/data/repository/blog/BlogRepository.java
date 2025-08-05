package com.edp.library.data.repository.blog;

import com.edp.library.data.entity.blog.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Find all blogs by a specific author
    List<Blog> findByAuthorId(Long authorId);

    Page<Blog> findAll(Specification<Blog> spec, Pageable pageable);
}