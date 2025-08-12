package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.LearningSubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningSubmissionTagRepository extends JpaRepository<LearningSubmissionTag, Long> {
}