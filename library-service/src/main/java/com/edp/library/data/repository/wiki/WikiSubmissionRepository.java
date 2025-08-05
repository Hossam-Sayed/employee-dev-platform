package com.edp.library.data.repository.wiki;

import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiSubmissionRepository extends JpaRepository<WikiSubmission, Long> {

    List<WikiSubmission> findByWikiIdOrderBySubmittedAtDesc(Long wikiId);

    List<WikiSubmission> findBySubmitterIdAndStatus(Long submitterId, SubmissionStatus status);

    List<WikiSubmission> findByReviewerIdAndStatus(Long reviewerId, SubmissionStatus status);

    // TODO: Remove?
    Optional<WikiSubmission> findByIdAndWikiId(Long id, Long wikiId);

    Page<WikiSubmission> findByWikiIdOrderBySubmittedAtDesc(Long wikiId, Pageable pageable);

    Page<WikiSubmission> findByReviewerIdAndStatus(Long reviewerId, SubmissionStatus submissionStatus, Pageable pageable);
}