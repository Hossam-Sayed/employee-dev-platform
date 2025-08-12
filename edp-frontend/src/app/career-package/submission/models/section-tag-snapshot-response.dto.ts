import { SubmissionTagSnapshotResponseDto } from './submission-tag-snapshot-response.dto';

export interface SubmissionSectionDto {
  sectionId: number;
  sectionName: string;
  sectionDescription: string;
  tags: SubmissionTagSnapshotResponseDto[];
}