import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TemplateSectionRequiredTagRequestDto } from '../models/template-section-required-tag-request.dto';
import { TemplateSectionRequiredTagResponseDto } from '../models/template-section-required-tag.dto';

@Injectable({ providedIn: 'root' })
export class SectionRequiredTagService {
  private httpClient = inject(HttpClient);
  private baseUrl = 'http://localhost:8081/api/section-required-tags';

    attachRequiredTag(
    request: TemplateSectionRequiredTagRequestDto
  ): Observable<TemplateSectionRequiredTagResponseDto> {
    return this.httpClient.post<TemplateSectionRequiredTagResponseDto>(
      this.baseUrl,
      request
    );
  }

  detachRequiredTag(requiredTagId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${requiredTagId}`);
  }
}
