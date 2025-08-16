package com.edp.library.data.repository.learning;

import com.edp.library.data.entity.learning.LearningSubmission;
import com.edp.library.data.enums.SubmissionStatus;
import com.edp.library.model.learning.UserApprovedLearningCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningSubmissionRepository extends JpaRepository<LearningSubmission, Long> {

    // Find all submissions for a given learning ID
    Page<LearningSubmission> findByLearningIdOrderBySubmittedAtDesc(Long learningId, Pageable pageable);

    // Find submissions waiting for review by a specific manager (reviewerId)
    Page<LearningSubmission> findBySubmitterIdInAndStatus(List<Long> submitterIds, SubmissionStatus status, Pageable pageable);

    @Query("SELECT new com.edp.library.model.learning.UserApprovedLearningCount(ls.submitterId, COUNT(ls)) " +
            "FROM LearningSubmission ls " +
            "WHERE ls.status = 'APPROVED' " +
            "GROUP BY ls.submitterId " +
            "ORDER BY COUNT(ls) DESC")
    Page<UserApprovedLearningCount> countApprovedLearningsBySubmitter(Pageable pageable);
}