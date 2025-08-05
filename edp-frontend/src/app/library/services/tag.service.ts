import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tag } from '../models/tag.model';
import { TagCreateRequest } from '../models/tag-create-request.model';
import { TagRequestResponse } from '../models/tag-request-response.model';

@Injectable({
  providedIn: 'root',
})
export class TagService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8082/api/tags/';

  getAllActiveTags(nameFilter?: string): Observable<Tag[]> {
    let params = new HttpParams();
    if (nameFilter) {
      params = params.set('nameFilter', nameFilter);
    }
    return this.http.get<Tag[]>(`${this.baseUrl}active`, { params });
  }

  createTagRequest(
    request: TagCreateRequest,
    requesterId: number
  ): Observable<TagRequestResponse> {
    const headers = { 'X-Requester-Id': requesterId.toString() };
    return this.http.post<TagRequestResponse>(
      `${this.baseUrl}requests`,
      request,
      {
        headers,
      }
    );
  }
}
