import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { Observable, catchError, EMPTY, finalize } from 'rxjs';
import { AuthService } from '../../auth/service/auth.service';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { BlogSubmissionResponse } from '../models/blog-submission-response.model';
import { LearningSubmissionResponse } from '../models/learning-submission-response.model';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { WikiSubmissionResponse } from '../models/wiki-submission-response.model';
import { LibraryService } from '../services/library.service';
import { MaterialType } from '../models/material.type';

type ReviewableSubmission =
  | LearningSubmissionResponse
  | BlogSubmissionResponse
  | WikiSubmissionResponse;

@Component({
  selector: 'app-pending-reviews',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    CustomTagComponent,
  ],
  templateUrl: './pending-reviews.component.html',
  styleUrls: ['./pending-reviews.component.css'],
})
export class PendingReviewsComponent implements OnInit {
  private authService = inject(AuthService);
  private libraryService = inject(LibraryService);
  private dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  currentTab = signal<MaterialType>('learning');

  learningsData = signal<PaginationResponse<LearningSubmissionResponse> | null>(
    null
  );
  blogsData = signal<PaginationResponse<BlogSubmissionResponse> | null>(null);
  wikisData = signal<PaginationResponse<WikiSubmissionResponse> | null>(null);

  isLoading = signal<boolean>(false);
  error = signal<string | null>(null);

  paginationRequest = signal<PaginationRequest>({
    page: 0,
    size: 10,
    sortBy: 'submittedAt',
    sortDirection: 'DESC',
  });

  displayedColumns: string[] = [
    'title',
    'submitterId',
    'submittedAt',
    'actions',
  ];
  readonly tabLabels = {
    learning: 'Learnings',
    blog: 'Blogs',
    wiki: 'Wikis',
  };

  ngOnInit(): void {
    this.fetchPendingSubmissions();
  }

  onTabChange(index: number): void {
    const tabNames: MaterialType[] = ['learning', 'blog', 'wiki'];
    this.currentTab.set(tabNames[index]);
    this.resetPagination();
    this.fetchPendingSubmissions();
  }

  fetchPendingSubmissions(): void {
    const managerId = this.authService.getUserId();
    if (!managerId) {
      this.error.set('Manager ID not found. Please log in.');
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);
    const request = this.paginationRequest();

    let fetchObservable: Observable<PaginationResponse<any>>;

    switch (this.currentTab()) {
      case 'learning':
        fetchObservable =
          this.libraryService.getPendingLearningSubmissions(request);
        break;
      case 'blog':
        fetchObservable =
          this.libraryService.getPendingBlogSubmissions(request);
        break;
      case 'wiki':
        fetchObservable =
          this.libraryService.getPendingWikiSubmissions(request);
        break;
      default:
        this.isLoading.set(false);
        return;
    }

    fetchObservable
      .pipe(
        finalize(() => this.isLoading.set(false)),
        catchError((err) => {
          this.error.set('Failed to fetch pending submissions.');
          console.error(
            `Error fetching pending ${this.currentTab()} submissions:`,
            err
          );
          return EMPTY;
        })
      )
      .subscribe((response) => {
        switch (this.currentTab()) {
          case 'learning':
            this.learningsData.set(
              response as PaginationResponse<LearningSubmissionResponse>
            );
            break;
          case 'blog':
            this.blogsData.set(
              response as PaginationResponse<BlogSubmissionResponse>
            );
            break;
          case 'wiki':
            this.wikisData.set(
              response as PaginationResponse<WikiSubmissionResponse>
            );
            break;
        }
        this.cdr.detectChanges();
      });
  }

  onPageChange(event: PageEvent): void {
    this.paginationRequest.update((req) => ({
      ...req,
      page: event.pageIndex,
      size: event.pageSize,
    }));
    this.fetchPendingSubmissions();
  }

  onSortChange(event: Sort): void {
    this.paginationRequest.update((req) => ({
      ...req,
      sortBy: event.active,
      sortDirection: event.direction.toUpperCase() as 'ASC' | 'DESC',
    }));
    this.fetchPendingSubmissions();
  }

  onViewDetails(submission: ReviewableSubmission): void {
    let dialogComponent;
    switch (this.currentTab()) {
      case 'learning':
        // dialogComponent = LearningSubmissionDetailsDialogComponent;
        break;
      case 'blog':
        // dialogComponent = BlogSubmissionDetailsDialogComponent;
        break;
      case 'wiki':
        // dialogComponent = WikiSubmissionDetailsDialogComponent;
        break;
    }

    if (dialogComponent) {
      this.dialog.open(dialogComponent, {
        data: submission,
        width: '600px',
        autoFocus: false,
      });
    }
  }

  resetPagination(): void {
    this.paginationRequest.set({
      page: 0,
      size: 10,
      sortBy: 'submittedAt',
      sortDirection: 'DESC',
    });
  }

  get tableData(): PaginationResponse<ReviewableSubmission> | null {
    switch (this.currentTab()) {
      case 'learning':
        return this.learningsData();
      case 'blog':
        return this.blogsData();
      case 'wiki':
        return this.wikisData();
      default:
        return null;
    }
  }

  get sortDirection(): SortDirection {
    return this.paginationRequest().sortDirection.toLowerCase() as
      | 'asc'
      | 'desc';
  }
}
