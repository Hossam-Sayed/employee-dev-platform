import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/service/auth.service';
import { BlogResponse } from '../models/blog-response.model';
import { LearningResponse } from '../models/learning-response.model';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { WikiResponse } from '../models/wiki-response.model';
import { BlogCreateRequest } from '../models/blog-create-request.model';
import { LearningCreateRequest } from '../models/learning-create-request.model';
import { WikiCreateRequest } from '../models/wiki-create-request.model';
import { BlogSubmissionResponse } from '../models/blog-submission-response.model';
import { LearningSubmissionResponse } from '../models/learning-submission-response.model';
import { WikiSubmissionResponse } from '../models/wiki-submission-response.model';

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

  createLearning(
    request: LearningCreateRequest,
    reviewerId: number
  ): Observable<LearningResponse> {
    const headers = {
      'X-Submitter-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.post<LearningResponse>(
      `${this.baseUrl}learnings`,
      request,
      { headers }
    );
  }

  createBlog(
    request: BlogCreateRequest,
    reviewerId: number
  ): Observable<BlogResponse> {
    const headers = {
      'X-Author-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.post<BlogResponse>(`${this.baseUrl}blogs`, request, {
      headers,
    });
  }

  createWiki(
    request: WikiCreateRequest,
    reviewerId: number
  ): Observable<WikiResponse> {
    const headers = {
      'X-Author-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.post<WikiResponse>(`${this.baseUrl}wikis`, request, {
      headers,
    });
  }

  getLearningDetails(learningId: number): Observable<LearningResponse> {
    return this.http.get<LearningResponse>(
      `${this.baseUrl}learnings/${learningId}`
    );
  }

  getBlogDetails(blogId: number): Observable<BlogResponse> {
    return this.http.get<BlogResponse>(`${this.baseUrl}blogs/${blogId}`);
  }

  getWikiDetails(wikiId: number): Observable<WikiResponse> {
    return this.http.get<WikiResponse>(`${this.baseUrl}wikis/${wikiId}`);
  }

  resubmitLearning(
    learningId: number,
    request: LearningCreateRequest,
    reviewerId: number
  ): Observable<LearningResponse> {
    const headers = {
      'X-Submitter-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.put<LearningResponse>(
      `${this.baseUrl}learnings/${learningId}/resubmit`,
      request,
      { headers }
    );
  }

  resubmitBlog(
    blogId: number,
    request: BlogCreateRequest,
    reviewerId: number
  ): Observable<BlogResponse> {
    const headers = {
      'X-Author-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.put<BlogResponse>(
      `${this.baseUrl}blogs/${blogId}/resubmit`,
      request,
      { headers }
    );
  }

  resubmitWiki(
    wikiId: number,
    request: WikiCreateRequest,
    reviewerId: number
  ): Observable<WikiResponse> {
    const headers = {
      'X-Author-Id': this.userId.toString(),
      'X-Reviewer-Id': reviewerId.toString(),
    };
    return this.http.put<WikiResponse>(
      `${this.baseUrl}wikis/${wikiId}/resubmit`,
      request,
      { headers }
    );
  }

  private buildParams(request: PaginationRequest): HttpParams {
    let params = new HttpParams();
    params = params.append('page', request.page);
    params = params.append('size', request.size);
    params = params.append('sortBy', request.sortBy);
    params = params.append('sortDirection', request.sortDirection);
    return params;
  }

  getLearningSubmissionHistory(
    learningId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<LearningSubmissionResponse>> {
    const params = this.buildParams(pagination);
    return this.http.get<PaginationResponse<LearningSubmissionResponse>>(
      `${this.baseUrl}learnings/${learningId}/history`,
      { params }
    );
  }

  getBlogSubmissionHistory(
    blogId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<BlogSubmissionResponse>> {
    const params = this.buildParams(pagination);
    return this.http.get<PaginationResponse<BlogSubmissionResponse>>(
      `${this.baseUrl}blogs/${blogId}/history`,
      { params }
    );
  }

  getWikiSubmissionHistory(
    wikiId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<WikiSubmissionResponse>> {
    const params = this.buildParams(pagination);
    return this.http.get<PaginationResponse<WikiSubmissionResponse>>(
      `${this.baseUrl}wikis/${wikiId}/history`,
      { params }
    );
  }

  getPendingLearningSubmissions(
    managerId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<LearningSubmissionResponse>> {
    const headers = { 'X-Reviewer-Id': managerId.toString() };
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<LearningSubmissionResponse>>(
      `${this.baseUrl}learnings/submissions/pending-review`,
      { headers, params }
    );
  }

  getPendingBlogSubmissions(
    managerId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<BlogSubmissionResponse>> {
    const headers = { 'X-Reviewer-Id': managerId.toString() };
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<BlogSubmissionResponse>>(
      `${this.baseUrl}blogs/submissions/pending-review`,
      { headers, params }
    );
  }

  getPendingWikiSubmissions(
    managerId: number,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<WikiSubmissionResponse>> {
    const headers = { 'X-Reviewer-Id': managerId.toString() };
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<WikiSubmissionResponse>>(
      `${this.baseUrl}wikis/submissions/pending-review`,
      { headers, params }
    );
  }
}
