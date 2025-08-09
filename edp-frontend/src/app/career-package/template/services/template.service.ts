import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { TemplateResponseDto } from '../models/template-response.dto';
import { Page } from '../../../shared/models/page.dto';
import { Observable } from 'rxjs';
import { TemplateRequestDto } from '../models/template-request.dto';
import { TemplateDetailResponseDto } from '../models/template-detail-response.dto';
import { TemplateUpdateRequestDto } from '../models/template-update-request.dto';

@Injectable({
  providedIn: 'root',
})
export class TemplateService {
  private baseUrl = 'http://localhost:8081/api/templates';
  private httpClient = inject(HttpClient);

  getTemplates(
    department: string,
    position: string,
    page = 0,
    size = 10
  ): Observable<Page<TemplateResponseDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (department) {
      params = params.set('department', department);
    }
    if (position) {
      params = params.set('position', position);
    }

    return this.httpClient.get<Page<TemplateResponseDto>>(this.baseUrl, {
      params,
    });
  }

  deleteTemplate(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`);
  }

  createTemplate(
    request: TemplateRequestDto
  ): Observable<TemplateDetailResponseDto> {
    return this.httpClient.post<TemplateDetailResponseDto>(
      this.baseUrl,
      request
    );
  }

  getTemplateById(id: number): Observable<TemplateDetailResponseDto> {
    return this.httpClient.get<TemplateDetailResponseDto>(
      `${this.baseUrl}/${id}`
    );
  }

  updateTemplate(
    id: number,
    request: TemplateUpdateRequestDto
  ): Observable<TemplateDetailResponseDto> {
    return this.httpClient.put<TemplateDetailResponseDto>(
      `${this.baseUrl}/${id}`,
      request
    );
  }
}
