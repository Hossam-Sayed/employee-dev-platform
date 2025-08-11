import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CareerPackageResponseDto } from '../models/package-detail-response.dto';

@Injectable({
  providedIn: 'root',
})
export class PackageService {
  private baseUrl = 'http://localhost:8081/api/career-package';
  private httpClient = inject(HttpClient);

  getCareerPackage(): Observable<CareerPackageResponseDto> {
    return this.httpClient.get<CareerPackageResponseDto>(this.baseUrl);
  }
}
