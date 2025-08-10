package com.edp.library.data.repository.wiki;

import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WikiSubmissionRepository extends JpaRepository<WikiSubmission, Long> {

    Page<WikiSubmission> findByWikiIdOrderBySubmittedAtDesc(Long wikiId, Pageable pageable);

    Page<WikiSubmission> findBySubmitterIdInAndStatus(List<Long> submitterIds, SubmissionStatus status, Pageable pageable);
}