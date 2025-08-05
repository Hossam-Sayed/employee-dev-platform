package com.edp.library.data.repository.blog;

import com.edp.library.data.entity.blog.BlogSubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogSubmissionTagRepository extends JpaRepository<BlogSubmissionTag, Long> {

    List<BlogSubmissionTag> findByBlogSubmissionId(Long blogSubmissionId);
}