package com.edp.library.data.repository.blog;

import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogSubmissionRepository extends JpaRepository<BlogSubmission, Long> {

    Page<BlogSubmission> findByBlogIdOrderBySubmittedAtDesc(Long blogId, Pageable pageable);

    List<BlogSubmission> findBySubmitterIdAndStatus(Long submitterId, SubmissionStatus status);

    Page<BlogSubmission> findByReviewerIdAndStatus(Long reviewerId, SubmissionStatus status, Pageable pageable);

    // TODO: Remove?
    Optional<BlogSubmission> findByIdAndBlogId(Long id, Long blogId);

    Optional<BlogSubmission> findByBlogAuthorIdAndTitleIgnoreCaseAndDocumentUrlIgnoreCase(Long authorId, String title, String documentUrl);
}