import { BlogTagResponse } from './blog-tag-response.model';
import { SubmissionStatus } from './submission-status.model';

export interface BlogSubmissionResponse {
  id: number; // Submission ID
  blogId: number;
  title: string;
  description: string;
  documentUrl: string;
  status: SubmissionStatus;
  reviewerComment: string;
  submitterId: number;
  submittedAt: string;
  reviewerId: number;
  reviewedAt: string;
  tags: BlogTagResponse[];
}
