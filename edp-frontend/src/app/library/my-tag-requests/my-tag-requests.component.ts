import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { catchError, EMPTY, finalize } from 'rxjs';
import { AuthService } from '../../auth/service/auth.service';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { TagRequestResponse } from '../models/tag-request-response.model';
import { TagService } from '../services/tag.service';
import { TagRequestDetailsDialogComponent } from '../tag-request-details-dialog/tag-request-details-dialog.component';
import { TagRequestDialogComponent } from '../tag-request-dialog/tag-request-dialog.component';

@Component({
  selector: 'app-my-tag-requests',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    CustomTagComponent,
  ],
  templateUrl: './my-tag-requests.component.html',
  styleUrls: ['./my-tag-requests.component.css'],
})
export class MyTagRequestsComponent implements OnInit {
  private tagService = inject(TagService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = [
    'requestedName',
    'status',
    'createdAt',
    'reviewedAt',
  ];
  tagRequests = signal<PaginationResponse<TagRequestResponse> | null>(null);
  isLoading = signal(true);
  error = signal<string | null>(null);

  // Pagination and sorting
  totalElements = signal(0);
  paginationRequest = signal<PaginationRequest>({
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortDirection: 'DESC',
  });

  ngOnInit(): void {
    this.fetchTagRequests();
  }

  fetchTagRequests(): void {
    const requesterId = this.authService.getUserId();
    if (!requesterId) {
      this.error.set('User ID not found. Please log in.');
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    this.tagService
      .getMyTagRequests(this.paginationRequest())
      .pipe(
        finalize(() => this.isLoading.set(false)),
        catchError((err) => {
          this.error.set('Failed to fetch tag requests.');
          console.error('Error fetching tag requests:', err);
          return EMPTY;
        })
      )
      .subscribe((response) => {
        this.tagRequests.set(response);
        this.totalElements.set(response.totalElements);
      });
  }

  onPageChange(event: PageEvent): void {
    this.paginationRequest.update((req) => ({
      ...req,
      page: event.pageIndex,
      size: event.pageSize,
    }));
    this.fetchTagRequests();
  }

  onSortChange(event: Sort): void {
    this.paginationRequest.update((req) => ({
      ...req,
      sortBy: event.active,
      sortDirection: event.direction.toUpperCase() as 'ASC' | 'DESC',
    }));
    this.fetchTagRequests();
  }

  onRowClick(tagRequest: TagRequestResponse): void {
    this.dialog.open(TagRequestDetailsDialogComponent, {
      data: tagRequest,
      width: '500px',
      autoFocus: false,
    });
  }

  onRequestTag(): void {
    const dialogRef = this.dialog.open(TagRequestDialogComponent, {
      width: '400px',
    });
    // this.router.navigate(['/library/request-tag']);
  }

  get sortDirection(): SortDirection {
    return this.paginationRequest().sortDirection.toLowerCase() as
      | 'asc'
      | 'desc';
  }
}
