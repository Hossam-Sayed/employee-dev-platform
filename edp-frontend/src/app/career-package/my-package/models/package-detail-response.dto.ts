import { SectionProgressResponseDto } from './section-progress-response.dto';
import { PackageProgressResponseDto } from './package-progress-response.dto';
import { SubmissionResponseDto } from './submission-response.dto';
import { CareerPackageStatusEnum } from './career-package-status.enum';

export interface CareerPackageResponseDto {
  id: number;
  department: string;
  position: string;
  createdAt: string; 
  updatedAt: string;
  status: CareerPackageStatusEnum;

  sections: SectionProgressResponseDto[];
  progress: PackageProgressResponseDto;
  submissions: SubmissionResponseDto[];
}

