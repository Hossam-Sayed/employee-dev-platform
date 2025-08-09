// career-package/template/template-section.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TemplateSectionRequestDto } from '../models/template-section-request.dto';
import { Observable } from 'rxjs';
import { TemplateSectionResponseDto } from '../models/template-section-response.dto';

@Injectable({ providedIn: 'root' })
export class TemplateSectionService {
  
  private baseUrl = 'http://localhost:8081/api/template-sections';
  private httpClient = inject(HttpClient);

  deleteSection(templateSectionId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${templateSectionId}`);
  }

  attachSection(dto: TemplateSectionRequestDto): Observable<TemplateSectionResponseDto> {
    return this.httpClient.post<TemplateSectionResponseDto>(this.baseUrl, dto);
  }
}
