import { User } from "../../../user/models/user.model";
import { SubmissionSectionDto } from "./section-tag-snapshot-response.dto";


export interface SubmissionSnapshotResponseDto {
  submissionId: number;
  user: User;
  department: string;
  position: string;
  status: string;
  comment: string;
  submittedAt: string; 
  reviewedAt: string; 
  sections: SubmissionSectionDto[];
}