import { TagRequestStatus } from './tag-request-status.type';

export interface TagRequestResponse {
  id: number;
  requestedName: string;
  requesterId: number;
  status: TagRequestStatus;
  reviewerComment: string | null;
  reviewerId: number | null;
  createdAt: string; // ISO 8601 string
  reviewedAt: string | null; // ISO 8601 string
}
