import { TemplateSectionRequiredTagResponseDto } from "./template-section-required-tag.dto";

export interface TemplateSectionResponseDto {
  id: number;
  name: string;
  description: string;
  requiredTags: TemplateSectionRequiredTagResponseDto[];
}
