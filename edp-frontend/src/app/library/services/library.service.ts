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
import { SubmissionStatus } from '../models/submission-status.model';
import { TagCreateRequest } from '../models/tag-create-request.model';
import { TagRequestResponse } from '../models/tag-request-response.model';
import { TagRequestReview } from '../models/tag-request-review.model';

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

  createBlog(request: BlogCreateRequest, file: File): Observable<BlogResponse> {
    const formData = new FormData();

    // Append the file with the key "file"
    formData.append('file', file);

    // Append the DTO as a JSON string with the key "blogCreateRequestDto"
    // The key here must match the @RequestPart name in your Spring Boot controller
    formData.append(
      'blogCreateRequestDto',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      })
    );

    // Send the formData object in the POST request
    return this.http.post<BlogResponse>(`${this.baseUrl}blogs`, formData);
  }

  createWiki(request: WikiCreateRequest, file: File): Observable<WikiResponse> {
    const formData = new FormData();

    // Append the file with the key "file"
    formData.append('file', file);

    // Append the DTO as a JSON string with the key "wikiCreateRequestDto"
    // The key here must match the @RequestPart name in your Spring Boot controller
    formData.append(
      'wikiCreateRequestDto',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      })
    );

    // Send the formData object in the POST request
    return this.http.post<WikiResponse>(`${this.baseUrl}wikis`, formData);
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
    request: BlogCreateRequest,
    file?: File | null // Added optional file parameter
  ): Observable<BlogResponse> {
    if (file) {
      const formData = new FormData();
      formData.append('file', file);
      formData.append(
        'blogCreateRequestDto',
        new Blob([JSON.stringify(request)], { type: 'application/json' })
      );
      return this.http.put<BlogResponse>(
        `${this.baseUrl}blogs/${blogId}/resubmit`,
        formData
      );
    } else {
      return this.http.put<BlogResponse>(
        `${this.baseUrl}blogs/${blogId}/resubmit`,
        request
      );
    }
  }

  resubmitWiki(
    wikiId: number,
    request: WikiCreateRequest,
    file?: File | null // Added optional file parameter
  ): Observable<WikiResponse> {
    if (file) {
      const formData = new FormData();
      formData.append('file', file);
      formData.append(
        'wikiCreateRequestDto',
        new Blob([JSON.stringify(request)], { type: 'application/json' })
      );
      return this.http.put<WikiResponse>(
        `${this.baseUrl}wikis/${wikiId}/resubmit`,
        formData
      );
    } else {
      return this.http.put<WikiResponse>(
        `${this.baseUrl}wikis/${wikiId}/resubmit`,
        request
      );
    }
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

  reviewLearningSubmission(
    matrialId: number,
    reviewRequest: { status: SubmissionStatus; reviewerComment: string | null }
  ) {
    return this.http.patch<LearningSubmissionResponse>(
      `${this.baseUrl}learnings/submissions/${matrialId}/review`,
      reviewRequest
    );
  }

  reviewBlogSubmission(
    matrialId: number,
    reviewRequest: { status: SubmissionStatus; reviewerComment: string | null }
  ) {
    return this.http.patch<BlogSubmissionResponse>(
      `${this.baseUrl}blogs/submissions/${matrialId}/review`,
      reviewRequest
    );
  }

  reviewWikiSubmission(
    matrialId: number,
    reviewRequest: { status: SubmissionStatus; reviewerComment: string | null }
  ) {
    return this.http.patch<WikiSubmissionResponse>(
      `${this.baseUrl}wikis/submissions/${matrialId}/review`,
      reviewRequest
    );
  }

  createTagRequest(request: TagCreateRequest): Observable<TagRequestResponse> {
    return this.http.post<TagRequestResponse>(
      `${this.baseUrl}tags/requests`,
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
      `${this.baseUrl}tags/my-requests`,
      { params }
    );
  }

  getAllPendingTagRequests(
    pagination: PaginationRequest
  ): Observable<PaginationResponse<TagRequestResponse>> {
    const params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString())
      .set('sortBy', pagination.sortBy)
      .set('sortDirection', pagination.sortDirection);

    return this.http.get<PaginationResponse<TagRequestResponse>>(
      `${this.baseUrl}tags/requests/pending`,
      { params }
    );
  }

  reviewTagRequest(
    tagRequestId: number,
    review: TagRequestReview
  ): Observable<TagRequestResponse> {
    return this.http.patch<TagRequestResponse>(
      `${this.baseUrl}tags/requests/${tagRequestId}/review`,
      review
    );
  }
}
