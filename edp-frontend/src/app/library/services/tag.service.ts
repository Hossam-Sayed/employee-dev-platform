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
  private readonly baseUrl = 'http://localhost:8083/api/tags';

  getAllActiveTags(query?: string): Observable<Tag[]> {
    let params = new HttpParams();
    if (query) {
      params = params.set('query', query);
    }
    return this.http.get<Tag[]>(this.baseUrl, { params });
  }

  addNewTag(request: TagCreateRequest): Observable<TagRequestResponse> {
    return this.http.post<TagRequestResponse>(this.baseUrl, {
      name: request.requestedName,
    });
  }
}
