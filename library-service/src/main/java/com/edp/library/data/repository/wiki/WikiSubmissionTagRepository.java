package com.edp.library.data.repository.wiki;

import com.edp.library.data.entity.wiki.WikiSubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiSubmissionTagRepository extends JpaRepository<WikiSubmissionTag, Long> {
}