import { TemplateSectionResponseDto } from "./template-section-response.dto";

export interface TemplateDetailResponseDto {
  id: number;
  department: string;
  position: string;
  createdAt: string; 
  sections: TemplateSectionResponseDto[];
}
