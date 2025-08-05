import { TagWithDuration } from './tag-with-duration.model';

export interface LearningCreateRequest {
  title: string;
  proofUrl: string;
  tags: TagWithDuration[];
}
