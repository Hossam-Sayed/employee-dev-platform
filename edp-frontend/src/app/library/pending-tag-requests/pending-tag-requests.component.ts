import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { LibraryService } from '../services/library.service';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { TagRequestResponse } from '../models/tag-request-response.model';
import { TagRequestDialogComponent } from '../tag-request-dialog/tag-request-dialog.component';
import { ReviewConfirmationDialogComponent } from '../review-confirmation-dialog/review-confirmation-dialog.component';
import { TagRequestReview } from '../models/tag-request-review.model';
import { Observable, tap, catchError, EMPTY } from 'rxjs';
import { TagRejectDialogComponent } from '../tag-reject-dialog/tag-reject-dialog.component';

@Component({
  selector: 'app-pending-tag-requests',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './pending-tag-requests.component.html',
  styleUrls: ['./pending-tag-requests.component.css'],
})
export class PendingTagRequestsComponent implements OnInit {
  private libraryService = inject(LibraryService);
  private cdr = inject(ChangeDetectorRef);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  tagRequestsData = signal<PaginationResponse<TagRequestResponse> | null>(null);
  isLoading = signal<boolean>(false);

  // Pagination and sorting
  paginationRequest = signal<PaginationRequest>({
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortDirection: 'DESC',
  });

  displayedColumns: string[] = [
    'requestedName',
    'requesterId',
    'createdAt',
    'actions',
  ];

  ngOnInit(): void {
    this.fetchPendingTagRequests();
  }

  fetchPendingTagRequests(): void {
    this.isLoading.set(true);
    const request = this.paginationRequest();

    this.libraryService.getAllPendingTagRequests(request).subscribe({
      next: (response) => {
        this.tagRequestsData.set(response);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch pending tag requests', err);
        this.isLoading.set(false);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.paginationRequest.update((req) => ({
      ...req,
      page: event.pageIndex,
      size: event.pageSize,
    }));
    this.fetchPendingTagRequests();
  }

  onSortChange(event: Sort): void {
    const sortDirection = event.direction.toUpperCase();
    this.paginationRequest.update((req) => ({
      ...req,
      sortBy: event.active,
      sortDirection: sortDirection as 'ASC' | 'DESC',
    }));
    this.fetchPendingTagRequests();
  }

  onApprove(request: TagRequestResponse): void {
    const dialogRef = this.dialog.open(ReviewConfirmationDialogComponent, {
      data: {
        message: 'Are you sure you want to approve this tag?',
        title: 'Approve Tag Request',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User confirmed, proceed with the review action
        const reviewRequest: TagRequestReview = {
          status: 'APPROVED',
          reviewerComment: null,
        };

        let reviewRequest$: Observable<TagRequestResponse>;

        reviewRequest$ = this.libraryService.reviewTagRequest(
          request.id,
          reviewRequest
        );

        reviewRequest$
          .pipe(
            tap(() => {
              // Reload the data or navigate after a successful review
              this.fetchPendingTagRequests();
            }),
            catchError((err) => {
              console.error('Failed to review tag request:', err);
              return EMPTY;
            })
          )
          .subscribe();
      }
    });
  }

  onReject(request: TagRequestResponse): void {
    const dialogRef = this.dialog.open(TagRejectDialogComponent, {
      width: '450px',
    });

    dialogRef.afterClosed().subscribe((comment: string) => {
      if (comment) {
        const reviewRequest: TagRequestReview = {
          status: 'REJECTED',
          reviewerComment: comment,
        };

        let reviewRequest$: Observable<TagRequestResponse>;

        reviewRequest$ = this.libraryService.reviewTagRequest(
          request.id,
          reviewRequest
        );

        reviewRequest$
          .pipe(
            tap(() => {
              // Reload the data or navigate after a successful review
              this.fetchPendingTagRequests();
            }),
            catchError((err) => {
              console.error('Failed to review tag request:', err);
              return EMPTY;
            })
          )
          .subscribe();
      }
    });
  }

  onCreateTagClick() {
    const dialogRef = this.dialog.open(TagRequestDialogComponent, {
      width: '400px',
    });
  }

  get totalElements(): number {
    return this.tagRequestsData()?.totalElements ?? 0;
  }

  get sortDirection(): SortDirection {
    return this.paginationRequest().sortDirection.toLowerCase() as
      | 'asc'
      | 'desc';
  }
}
