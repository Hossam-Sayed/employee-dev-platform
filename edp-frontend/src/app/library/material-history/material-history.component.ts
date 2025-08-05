import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { catchError, EMPTY, Observable, switchMap, tap } from 'rxjs';
import { LibraryService } from '../../library/services/library.service';
import { MaterialType } from '../../library/models/material.type';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { BlogSubmissionResponse } from '../models/blog-submission-response.model';
import { LearningSubmissionResponse } from '../models/learning-submission-response.model';
import { WikiSubmissionResponse } from '../models/wiki-submission-response.model';
import { BlogTagResponse } from '../models/blog-tag-response.model';
import { LearningTagResponse } from '../models/learning-tag-response.model';
import { WikiTagResponse } from '../models/wiki-tag-response.model';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { MatChipListbox } from '@angular/material/chips';
import { MatList, MatListItem } from '@angular/material/list';

type SubmissionResponse =
  | LearningSubmissionResponse
  | BlogSubmissionResponse
  | WikiSubmissionResponse;

@Component({
  selector: 'app-material-history',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatDividerModule,
    MatExpansionModule,
    CustomTagComponent,
    MatChipListbox,
    MatListItem,
    MatList,
  ],
  templateUrl: './material-history.component.html',
  styleUrls: ['./material-history.component.css'],
})
export class MaterialHistoryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private libraryService = inject(LibraryService);

  materialType = signal<MaterialType | null>(null);
  materialId = signal<number | null>(null);
  submissionHistory = signal<PaginationResponse<SubmissionResponse> | null>(
    null
  );
  isLoading = signal(true);
  error = signal<string | null>(null);

  // Pagination properties
  pageIndex = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        tap((params) => {
          const materialId = Number(params.get('materialId'));
          const type = params.get('type') as MaterialType;
          this.materialId.set(materialId);
          this.materialType.set(type);

          if (!materialId || !type) {
            this.error.set('Invalid URL. Missing material type or ID.');
          }
        }),
        switchMap(() => {
          if (this.error()) {
            return EMPTY;
          }
          const paginationRequest: PaginationRequest = {
            page: this.pageIndex(),
            size: this.pageSize(),
            sortBy: 'submittedAt',
            sortDirection: 'DESC',
          };
          return this.getSubmissionHistory(
            this.materialId()!,
            this.materialType()!,
            paginationRequest
          );
        }),
        tap((response) => {
          this.submissionHistory.set(response);
          this.totalElements.set(response.totalElements);
          this.isLoading.set(false);
        }),
        catchError((err) => {
          console.error('Error fetching submission history:', err);
          this.error.set('Failed to fetch submission history.');
          this.isLoading.set(false);
          return EMPTY;
        })
      )
      .subscribe();
  }

  handlePageEvent(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.isLoading.set(true);
    const paginationRequest: PaginationRequest = {
      page: this.pageIndex(),
      size: this.pageSize(),
      sortBy: 'submittedAt',
      sortDirection: 'DESC',
    };
    this.getSubmissionHistory(
      this.materialId()!,
      this.materialType()!,
      paginationRequest
    )
      .pipe(
        tap((response) => {
          this.submissionHistory.set(response);
          this.totalElements.set(response.totalElements);
          this.isLoading.set(false);
        }),
        catchError((err) => {
          console.error('Error fetching submission history:', err);
          this.error.set('Failed to fetch submission history.');
          this.isLoading.set(false);
          return EMPTY;
        })
      )
      .subscribe();
  }

  getSubmissionHistory(
    id: number,
    type: MaterialType,
    pagination: PaginationRequest
  ): Observable<PaginationResponse<SubmissionResponse>> {
    switch (type) {
      case 'learning':
        return this.libraryService.getLearningSubmissionHistory(id, pagination);
      case 'blog':
        return this.libraryService.getBlogSubmissionHistory(id, pagination);
      case 'wiki':
        return this.libraryService.getWikiSubmissionHistory(id, pagination);
      default:
        this.error.set('Unsupported material type.');
        return EMPTY;
    }
  }

  isLearningSubmission(
    submission: SubmissionResponse
  ): submission is LearningSubmissionResponse {
    return 'proofUrl' in submission;
  }

  isLearningTag(
    tag: LearningTagResponse | BlogTagResponse | WikiTagResponse
  ): tag is LearningTagResponse {
    return (tag as LearningTagResponse).durationMinutes !== undefined;
  }

  getDurationMinutes(
    tags: (LearningTagResponse | BlogTagResponse | WikiTagResponse)[]
  ): number | null {
    const learningTag = tags.find(this.isLearningTag);
    return learningTag ? learningTag.durationMinutes : null;
  }
}
