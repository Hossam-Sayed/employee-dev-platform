import { LearningTagResponse } from './learning-tag-response.model';
import { SubmissionStatus } from './submission-status.model';

export interface LearningResponse {
  id: number;
  employeeId: number;
  currentSubmissionId: number;
  title: string;
  proofUrl: string;
  status: SubmissionStatus;
  tags: LearningTagResponse[];
  createdAt: string;
  updatedAt: string;
}
