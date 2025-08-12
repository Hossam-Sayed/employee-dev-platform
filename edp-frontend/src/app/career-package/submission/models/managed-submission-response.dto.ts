import { User } from "../../../user/models/user.model";

export interface ManagedSubmissionResponseDto {
    id: number;
    submittedAt: string;
    status: string;
    comment: string;
    reviewedAt: string;
    user: User;
}