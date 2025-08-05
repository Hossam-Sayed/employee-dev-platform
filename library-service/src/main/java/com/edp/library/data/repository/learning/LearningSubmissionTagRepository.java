package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.LearningSubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningSubmissionTagRepository extends JpaRepository<LearningSubmissionTag, Long> {

    // Find all tags associated with a specific learning submission
    List<LearningSubmissionTag> findByLearningSubmissionId(Long learningSubmissionId);
}