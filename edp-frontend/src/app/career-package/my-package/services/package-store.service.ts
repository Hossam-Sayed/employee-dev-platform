import { Injectable, signal } from '@angular/core';
import { CareerPackageResponseDto } from '../models/package-detail-response.dto';

@Injectable({ providedIn: 'root' })
export class PackageDetailService {
  private _careerPackage = signal<CareerPackageResponseDto | null>(null);

  package = this._careerPackage.asReadonly();

  setCareerPackage(careerPackage: CareerPackageResponseDto) {
    this._careerPackage.set(careerPackage);
  }

  submitCareerPackage() {
    const updatedPackage = {
      ...this._careerPackage()!,
      status: 'SUBMITTED',
    };
  }
}
