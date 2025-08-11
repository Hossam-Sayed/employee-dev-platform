import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SectionProgressResponseDto } from '../models/section-progress-response.dto';
import { TagProgressResponseDto } from '../models/tag-progress-reponse.dto';

@Injectable({
  providedIn: 'root',
})
export class SectionProgressService {
  private baseUrl = 'http://localhost:8081/api/career-package-sections';
  private httpClient = inject(HttpClient);

  getSectionProgress(sectionProgressId: number): Observable<SectionProgressResponseDto> {
    return this.httpClient.get<SectionProgressResponseDto>(`${this.baseUrl}/${sectionProgressId}`);
  }

  listTagsProgress(sectionProgressId: number): Observable<TagProgressResponseDto[]> {
    return this.httpClient.get<TagProgressResponseDto[]>(`${this.baseUrl}/${sectionProgressId}/tags-progress`);
  }
}