package com.edp.library.data.repository.blog;

import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogSubmissionRepository extends JpaRepository<BlogSubmission, Long> {

    Page<BlogSubmission> findByBlogIdOrderBySubmittedAtDesc(Long blogId, Pageable pageable);

    Page<BlogSubmission> findBySubmitterIdInAndStatus(List<Long> submitterIds, SubmissionStatus status, Pageable pageable);
}