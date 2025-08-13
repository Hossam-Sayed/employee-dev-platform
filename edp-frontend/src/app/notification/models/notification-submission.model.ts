import { SubmissionStatus } from '../../library/models/submission-status.model';
import { SubmissionType } from './submission-type.model';

export interface NotificationSubmission {
  id: string;
  submissionId: number;
  submissionType: SubmissionType;
  title: string;
  ownerId: number;
  actorId: number;
  status: SubmissionStatus;
  read: boolean;
  createdAt: Date;
}
