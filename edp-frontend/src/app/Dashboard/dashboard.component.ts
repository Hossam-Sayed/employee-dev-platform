import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSortModule } from '@angular/material/sort';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { LibraryService } from '../library/services/library.service';
import { ApprovedLearningByEmployeeResponse } from '../library/models/approved-learning-by-employee-response.model';
import { PaginationRequest } from '../library/models/pagination-request.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  private libraryService = inject(LibraryService);
  private snackBar = inject(MatSnackBar);

  loading = signal(true);
  employeeRankings = signal<ApprovedLearningByEmployeeResponse[]>([]);
  totalElements = signal(0);
  pageSize = signal(10);
  pageIndex = signal(0);

  displayedColumns: string[] = ['position', 'username', 'count'];

  ngOnInit(): void {
    this.fetchRankingData();
  }

  fetchRankingData(event?: PageEvent): void {
    this.loading.set(true);

    const paginationRequest: PaginationRequest = {
      page: event ? event.pageIndex : this.pageIndex(),
      size: event ? event.pageSize : this.pageSize(),
      sortBy: 'none', 
      sortDirection: 'ASC', 
    };

    this.libraryService.getApprovedLearningsRanking(paginationRequest).subscribe({
      next: (data) => {
        this.employeeRankings.set(data.content);
        this.totalElements.set(data.totalElements);
        this.pageSize.set(data.size);
        this.pageIndex.set(data.page);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load ranking data', err);
        this.snackBar.open('Failed to load ranking data.', 'Close', {
          duration: 3000,
        });
        this.loading.set(false);
      },
    });
  }

  handlePageEvent(event: PageEvent): void {
    this.fetchRankingData(event);
  }
}