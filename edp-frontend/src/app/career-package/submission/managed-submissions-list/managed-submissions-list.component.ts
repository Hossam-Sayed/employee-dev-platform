import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ManagedSubmissionResponseDto } from '../models/managed-submission-response.dto';
import { SubmissionService } from '../../services/submission-service';

@Component({
  selector: 'app-managed-submissions-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    RouterLink,
    DatePipe
  ],
  templateUrl: './managed-submissions-list.component.html',
  styleUrls: ['./managed-submissions-list.component.css']
})
export class ManagedSubmissionsListComponent implements OnInit {
  private submissionService = inject(SubmissionService);
  private snackBar = inject(MatSnackBar);

  loading = signal(true);
  submissions = signal<ManagedSubmissionResponseDto[]>([]);

  ngOnInit(): void {
    this.loadManagedSubmissions();
  }

  loadManagedSubmissions(): void {
    this.loading.set(true);
    this.submissionService.getManagedSubmissions().pipe(
      catchError(err => {
        this.snackBar.open('Failed to load managed submissions.', 'Close', { duration: 3000 });
        this.loading.set(false);
        return of([]);
      })
    ).subscribe(data => {
      this.submissions.set(data);
      this.loading.set(false);
    });
  }
}