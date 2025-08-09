import { CriteriaType } from './criteria-type.enum';

export interface TemplateSectionRequiredTagRequestDto {
  templateSectionId: number; 
  tagId: number;
  criteriaType: CriteriaType;
  criteriaMinValue: number;
}
