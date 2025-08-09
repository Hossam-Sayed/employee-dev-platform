import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SectionResponseDto } from '../models/section-response.dto';
import { SectionRequestDto } from '../models/section-request.dto';

@Injectable({ providedIn: 'root' })
export class SectionService {
  private httpClient = inject(HttpClient);
  private baseUrl = 'http://localhost:8081/api/sections';

  searchSections(query: string): Observable<SectionResponseDto[]> {
    return this.httpClient.get<SectionResponseDto[]>(this.baseUrl, {
      params: { query },
    });
  }

  createSection(dto: SectionRequestDto): Observable<SectionResponseDto> {
    return this.httpClient.post<SectionResponseDto>(this.baseUrl, dto);
  }
}
