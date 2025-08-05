import { SubmissionStatus } from './submission-status.model';
import { WikiTagResponse } from './wiki-tag-response.model';

export interface WikiResponse {
  id: number;
  authorId: number;
  currentSubmissionId: number;
  title: string;
  description: string;
  documentUrl: string;
  status: SubmissionStatus;
  tags: WikiTagResponse[];
  createdAt: string;
  updatedAt: string;
}
