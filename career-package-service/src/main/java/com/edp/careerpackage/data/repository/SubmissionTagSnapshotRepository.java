package com.edp.careerpackage.data.repository;

import com.edp.careerpackage.data.entity.SubmissionTagSnapshot;
import com.edp.careerpackage.data.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionTagSnapshotRepository extends JpaRepository<SubmissionTagSnapshot, Long> {

    List<SubmissionTagSnapshot> findBySubmission(Submission submission);

    void deleteAllBySubmission(Submission submission);
}
