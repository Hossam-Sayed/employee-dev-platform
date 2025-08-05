import { BlogTagResponse } from './blog-tag-response.model';
import { SubmissionStatus } from './submission-status.model';

export interface BlogResponse {
  id: number;
  authorId: number;
  currentSubmissionId: number;
  title: string;
  description: string;
  documentUrl: string;
  status: SubmissionStatus;
  tags: BlogTagResponse[];
  createdAt: string;
  updatedAt: string;
}
