export interface TagRequestReview {
  status: 'APPROVED' | 'REJECTED';
  reviewerComment: string | null;
}
