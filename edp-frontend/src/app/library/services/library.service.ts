import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/service/auth.service';
import { BlogResponse } from '../models/blog-response.model';
import { LearningResponse } from '../models/learning-response.model';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { WikiResponse } from '../models/wiki-response.model';

@Injectable({
  providedIn: 'root',
})
export class LibraryService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly baseUrl = 'http://localhost:8082/api/';

  private get userId(): number {
    const userId = this.authService.getUserId();
    if (!userId) {
      throw new Error('User is not authenticated.');
    }
    return userId;
  }

  private createHttpParams(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): HttpParams {
    let params = new HttpParams()
      .set('page', request.page)
      .set('size', request.size)
      .set('sortBy', request.sortBy)
      .set('sortDirection', request.sortDirection);

    if (statusFilter) {
      params = params.set('statusFilter', statusFilter);
    }
    if (tagIdFilter != null) {
      params = params.set('tagIdFilter', tagIdFilter.toString());
    }

    return params;
  }

  getMyLearnings(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): Observable<PaginationResponse<LearningResponse>> {
    const params = this.createHttpParams(request, statusFilter, tagIdFilter);
    const headers = { 'X-Employee-Id': this.userId.toString() };
    return this.http.get<PaginationResponse<LearningResponse>>(
      `${this.baseUrl}learnings/my-learnings`,
      { params, headers }
    );
  }

  getMyBlogs(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): Observable<PaginationResponse<BlogResponse>> {
    const params = this.createHttpParams(request, statusFilter, tagIdFilter);
    const headers = { 'X-Author-Id': this.userId.toString() };
    return this.http.get<PaginationResponse<BlogResponse>>(
      `${this.baseUrl}blogs/my-blogs`,
      { params, headers }
    );
  }

  getMyWikis(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): Observable<PaginationResponse<WikiResponse>> {
    const params = this.createHttpParams(request, statusFilter, tagIdFilter);
    const headers = { 'X-Author-Id': this.userId.toString() };
    return this.http.get<PaginationResponse<WikiResponse>>(
      `${this.baseUrl}wikis/my-wikis`,
      { params, headers }
    );
  }
}
