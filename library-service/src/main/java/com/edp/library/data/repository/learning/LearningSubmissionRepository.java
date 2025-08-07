package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSubmissionRepository extends JpaRepository<LearningSubmission, Long> {

    // Find all submissions for a given learning ID
    Page<LearningSubmission> findByLearningIdOrderBySubmittedAtDesc(Long learningId, Pageable pageable);

    // Find submissions waiting for review by a specific manager (reviewerId)
    Page<LearningSubmission> findBySubmitterIdInAndStatus(List<Long> submitterIds, SubmissionStatus status, Pageable pageable);

    Optional<LearningSubmission> findByLearningEmployeeIdAndTitleIgnoreCaseAndProofUrlIgnoreCase(Long employeeId, String title, String proofUrl);
}