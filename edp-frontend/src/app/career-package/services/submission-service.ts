import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubmissionResponseDto } from '../my-package/models/submission-response.dto';
import { SubmissionSnapshotResponseDto } from '../submission/models/submission-snapshot-response.dto';
import { ManagedSubmissionResponseDto } from '../submission/models/managed-submission-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SubmissionService {
  private baseUrl = 'http://localhost:8081/api/submissions';
  private httpClient = inject(HttpClient);

  submitCareerPackage(): Observable<SubmissionResponseDto> {
    return this.httpClient.post<SubmissionResponseDto>(this.baseUrl, null);
  }

  getSubmissionDetails(
    submissionId: number
  ): Observable<SubmissionSnapshotResponseDto> {
    return this.httpClient.get<SubmissionSnapshotResponseDto>(
      `${this.baseUrl}/${submissionId}`
    );
  }

  getManagedSubmissions(): Observable<ManagedSubmissionResponseDto[]> {
    return this.httpClient.get<ManagedSubmissionResponseDto[]>(`${this.baseUrl}/search`);
  }

  approveSubmission(
    submissionId: number,
    comment: string
  ): Observable<SubmissionResponseDto> {
    return this.httpClient.put<SubmissionResponseDto>(
      `${this.baseUrl}/${submissionId}/approve`,
      { comment }
    );
  }

  rejectSubmission(
    submissionId: number,
    comment: string
  ): Observable<SubmissionResponseDto> {
    return this.httpClient.put<SubmissionResponseDto>(
      `${this.baseUrl}/${submissionId}/reject`,
      { comment }
    );
  }
}
