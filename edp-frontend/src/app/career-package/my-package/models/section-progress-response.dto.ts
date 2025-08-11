import { TagProgressResponseDto } from "./tag-progress-reponse.dto";

export interface SectionProgressResponseDto {
  sectionProgressId: number;
  sectionName: string;
  sectionDescription: string;
  sectionProgressPercent: number;
  tags: TagProgressResponseDto[];
}
