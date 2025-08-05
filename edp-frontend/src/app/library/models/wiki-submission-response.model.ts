import { SubmissionStatus } from './submission-status.model';
import { WikiTagResponse } from './wiki-tag-response.model';

export interface WikiSubmissionResponse {
  id: number; // Submission ID
  wikiId: number;
  title: string;
  description: string;
  documentUrl: string;
  status: SubmissionStatus;
  reviewerComment: string;
  submitterId: number;
  submittedAt: string;
  reviewerId: number;
  reviewedAt: string;
  tags: WikiTagResponse[];
}
