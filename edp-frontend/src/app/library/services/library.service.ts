import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
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
  private readonly baseUrl = 'http://localhost:8082/api/';

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
    return this.http.get<PaginationResponse<LearningResponse>>(
      `${this.baseUrl}learnings/my-learnings`,
      { params }
    );
  }

  getMyBlogs(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): Observable<PaginationResponse<BlogResponse>> {
    const params = this.createHttpParams(request, statusFilter, tagIdFilter);
    return this.http.get<PaginationResponse<BlogResponse>>(
      `${this.baseUrl}blogs/my-blogs`,
      { params }
    );
  }

  getMyWikis(
    request: PaginationRequest,
    statusFilter?: string | null,
    tagIdFilter?: number | null
  ): Observable<PaginationResponse<WikiResponse>> {
    const params = this.createHttpParams(request, statusFilter, tagIdFilter);
    return this.http.get<PaginationResponse<WikiResponse>>(
      `${this.baseUrl}wikis/my-wikis`,
      { params }
    );
  }

  createLearning(request: LearningCreateRequest): Observable<LearningResponse> {
    return this.http.post<LearningResponse>(
      `${this.baseUrl}learnings`,
      request
    );
  }

  createBlog(request: BlogCreateRequest): Observable<BlogResponse> {
    return this.http.post<BlogResponse>(`${this.baseUrl}blogs`, request);
  }

  createWiki(request: WikiCreateRequest): Observable<WikiResponse> {
    return this.http.post<WikiResponse>(`${this.baseUrl}wikis`, request);
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
    request: LearningCreateRequest
  ): Observable<LearningResponse> {
    return this.http.put<LearningResponse>(
      `${this.baseUrl}learnings/${learningId}/resubmit`,
      request
    );
  }

  resubmitBlog(
    blogId: number,
    request: BlogCreateRequest
  ): Observable<BlogResponse> {
    return this.http.put<BlogResponse>(
      `${this.baseUrl}blogs/${blogId}/resubmit`,
      request
    );
  }

  resubmitWiki(
    wikiId: number,
    request: WikiCreateRequest
  ): Observable<WikiResponse> {
    return this.http.put<WikiResponse>(
      `${this.baseUrl}wikis/${wikiId}/resubmit`,
      request
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
    pagination: PaginationRequest
  ): Observable<PaginationResponse<LearningSubmissionResponse>> {
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<LearningSubmissionResponse>>(
      `${this.baseUrl}learnings/submissions/pending-review`,
      { params }
    );
  }

  getPendingBlogSubmissions(
    pagination: PaginationRequest
  ): Observable<PaginationResponse<BlogSubmissionResponse>> {
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<BlogSubmissionResponse>>(
      `${this.baseUrl}blogs/submissions/pending-review`,
      { params }
    );
  }

  getPendingWikiSubmissions(
    pagination: PaginationRequest
  ): Observable<PaginationResponse<WikiSubmissionResponse>> {
    const params = this.createHttpParams(pagination);

    return this.http.get<PaginationResponse<WikiSubmissionResponse>>(
      `${this.baseUrl}wikis/submissions/pending-review`,
      { params }
    );
  }
}
