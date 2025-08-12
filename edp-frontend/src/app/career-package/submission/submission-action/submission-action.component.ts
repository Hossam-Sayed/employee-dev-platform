import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';

import { SubmissionDetailComponent } from '../submission-detail/submission-detail.component';
import { finalize } from 'rxjs/operators';
import { catchError, of, throwError } from 'rxjs';
import { SubmissionService } from '../../services/submission-service';

@Component({
  selector: 'app-submission-action',
  standalone: true,
  imports: [
    CommonModule,
    SubmissionDetailComponent,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './submission-action.component.html',
  styleUrls: ['./submission-action.component.css'],
})
export class SubmissionActionComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private submissionService = inject(SubmissionService);
  private snackBar = inject(MatSnackBar);

  submissionId = signal<number | null>(null);
  submitting = signal(false);

  commentForm = new FormGroup({
    comment: new FormControl('', Validators.required),
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('submissionId'));
      if (!isNaN(id)) {
        this.submissionId.set(id);
      } else {
        this.router.navigate(['/']);
      }
    });
  }

  onApprove(): void {
    if (this.commentForm.invalid || !this.submissionId()) {
      this.snackBar.open('Please add a comment before approving.', 'Close', {
        duration: 3000,
      });
      return;
    }

    this.submitting.set(true);
    this.submissionService
      .approveSubmission(this.submissionId()!, this.commentForm.value.comment!)
      .pipe(
        finalize(() => this.submitting.set(false)),
        catchError((err) => {
          this.snackBar.open(
            'Failed to approve submission:' + err.error.message,
            'Close',
            { duration: 3000 }
          );
          return throwError(() => err);
        })
      )
      .subscribe((res) => {
        this.snackBar.open('Submission approved successfully.', 'Close', {
          duration: 3000,
        });
        this.router.navigate(['/career-package/submissions/managed']);
      });
  }

  onReject(): void {
    if (this.commentForm.invalid || !this.submissionId()) {
      this.snackBar.open('Please add a comment before rejecting.', 'Close', {
        duration: 3000,
      });
      return;
    }

    this.submitting.set(true);
    this.submissionService
      .rejectSubmission(this.submissionId()!, this.commentForm.value.comment!)
      .pipe(
        finalize(() => this.submitting.set(false)),
        catchError((err) => {
          this.snackBar.open(
            'Failed to reject submission:' + err.error.message,
            'Close',
            { duration: 3000 }
          );
          return throwError(() => err);
        })
      )
      .subscribe(() => {
        this.snackBar.open('Submission rejected successfully.', 'Close', {
          duration: 3000,
        });
        this.router.navigate(['/career-package/submissions/managed']);
      });
  }
}
