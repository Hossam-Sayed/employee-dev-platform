import { LearningTagResponse } from './learning-tag-response.model';
import { SubmissionStatus } from './submission-status.model';

export interface LearningSubmissionResponse {
  id: number; // Submission ID
  learningId: number;
  title: string;
  proofUrl: string;
  status: SubmissionStatus;
  reviewerComment: string;
  submitterId: number;
  submittedAt: string;
  reviewerId: number;
  reviewedAt: string;
  tags: LearningTagResponse[];
}
