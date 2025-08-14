import { Injectable, signal } from '@angular/core';
import { CareerPackageResponseDto } from '../models/package-detail-response.dto';
import { CareerPackageStatusEnum } from '../models/career-package-status.enum';

@Injectable({ providedIn: 'root' })
export class PackageDetailService {
  private _careerPackage = signal<CareerPackageResponseDto | null>(null);

  package = this._careerPackage.asReadonly();

  setCareerPackage(careerPackage: CareerPackageResponseDto) {
    this._careerPackage.set(careerPackage);
  }

  submitCareerPackage(submission: any) {
    this._careerPackage.set({
      ...this._careerPackage()!,
      status: CareerPackageStatusEnum.SUBMITTED,
      submissions: [...(this._careerPackage()?.submissions || []), submission],
    });
  }
}
