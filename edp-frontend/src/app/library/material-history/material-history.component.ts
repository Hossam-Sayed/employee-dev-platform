import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser'; // Import DomSanitizer and SafeResourceUrl
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
import { FileService } from '../../library/services/file.service'; // Import FileService
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
  private fileService = inject(FileService);
  private sanitizer = inject(DomSanitizer);

  materialType = signal<MaterialType | null>(null);
  materialId = signal<number | null>(null);
  submissionHistory = signal<PaginationResponse<SubmissionResponse> | null>(
    null
  );
  isLoading = signal(true);
  error = signal<string | null>(null);

  // Cache for fetched file URLs
  fileUrls = signal<Map<number, SafeResourceUrl>>(new Map());

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

  /**
   * Fetches the file for a submission if it hasn't been fetched before.
   * This method is triggered when a panel is opened.
   */
  onPanelOpened(submission: SubmissionResponse): void {
    const isBlogOrWiki = !this.isLearningSubmission(submission);
    const hasDocumentId = 'documentId' in submission && submission.documentId;

    if (isBlogOrWiki && hasDocumentId && !this.fileUrls().has(submission.id)) {
      this.fileService
        .getFile(submission.documentId)
        .pipe(
          tap((blob) => {
            const url = URL.createObjectURL(blob);
            const safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
            this.fileUrls.update((map) => map.set(submission.id, safeUrl));
          }),
          catchError((err) => {
            console.error(
              `Error fetching file for submission ${submission.id}:`,
              err
            );
            // Optional: handle error state for a single submission
            return EMPTY;
          })
        )
        .subscribe();
    }
  }

  /**
   * Retrieves the cached file URL for a given submission ID.
   */
  getSubmissionFileUrl(submissionId: number): SafeResourceUrl | undefined {
    return this.fileUrls().get(submissionId);
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
