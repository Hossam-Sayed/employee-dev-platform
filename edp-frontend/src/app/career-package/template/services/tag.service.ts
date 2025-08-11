import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TagResponseDto } from '../models/tag-response.dto';
import { TagRequestDto } from '../models/tag-request.dto';

@Injectable({ providedIn: 'root' })
export class TagService {
  private httpClient = inject(HttpClient);
  private baseUrl = 'http://localhost:8083/api/tags';

  searchTags(query: string): Observable<TagResponseDto[]> {
    return this.httpClient.get<TagResponseDto[]>(this.baseUrl, {
      params: { query },
    });
  }

  createTag(dto: TagRequestDto): Observable<TagResponseDto> {
    return this.httpClient.post<TagResponseDto>(this.baseUrl, dto);
  }
}
