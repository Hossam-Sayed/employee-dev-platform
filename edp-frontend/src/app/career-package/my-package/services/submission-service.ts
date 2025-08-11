import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubmissionResponseDto } from '../models/submission-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SubmissionService {
  private baseUrl = 'http://localhost:8081/api/submissions';
  private httpClient = inject(HttpClient);

  submitCareerPackage(): Observable<SubmissionResponseDto> {
    return this.httpClient.post<SubmissionResponseDto>(this.baseUrl, null);
  }
}