import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tag } from '../models/tag.model';
import { TagCreateRequest } from '../models/tag-create-request.model';
import { TagRequestResponse } from '../models/tag-request-response.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { PaginationRequest } from '../models/pagination-request.model';

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

  createTagRequest(request: TagCreateRequest): Observable<TagRequestResponse> {
    return this.http.post<TagRequestResponse>(
      `${this.baseUrl}requests`,
      request
    );
  }

  getMyTagRequests(
    pagination: PaginationRequest
  ): Observable<PaginationResponse<TagRequestResponse>> {
    const params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString())
      .set('sortBy', pagination.sortBy)
      .set('sortDirection', pagination.sortDirection);

    return this.http.get<PaginationResponse<TagRequestResponse>>(
      `${this.baseUrl}my-requests`,
      { params }
    );
  }

  addNewTag(request: TagCreateRequest): Observable<TagRequestResponse> {
    return this.http.post<TagRequestResponse>(
      `${this.baseUrl}requests`,
      request
    );
  }
}
