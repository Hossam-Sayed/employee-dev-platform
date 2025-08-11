export interface TagProgressResponseDto {
  tagProgressId: number;
  tagName: string;
  criteriaType: string;
  requiredValue: number;
  completedValue: number;
  proofUrl: string;
  fileId: string;
}
