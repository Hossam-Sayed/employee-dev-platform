import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { SubmissionService } from '../../services/submission-service';
import { FileService } from '../../services/file.service';
import { SubmissionSnapshotResponseDto } from '../models/submission-snapshot-response.dto';
import { SubmissionTagSnapshotResponseDto } from '../models/submission-tag-snapshot-response.dto';

@Component({
  selector: 'app-submission-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    RouterLink,
    DatePipe,
  ],
  templateUrl: './submission-detail.component.html',
  styleUrls: ['./submission-detail.component.css'],
})
export class SubmissionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private submissionService = inject(SubmissionService);
  private fileService = inject(FileService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);
  submissionId = input<number | null | undefined>();

  loading = signal(true);
  submissionData = signal<SubmissionSnapshotResponseDto | null>(null);

  backRoute: string | undefined;

  ngOnInit(): void {
    const urlSegments = this.router.url.split('/');
    const lastParentSegment = urlSegments[urlSegments.length - 2];

    if (lastParentSegment === 'action') {
      this.backRoute = '/career-package/submissions/managed';
    } else {
      this.backRoute = '/career-package/my-package';
    }

    if (this.submissionId()) {
      this.loadSubmissionDetails(this.submissionId()!);
    } else {
      this.route.paramMap.subscribe((params) => {
        const idFromRoute = Number(params.get('submissionId'));
        if (isNaN(idFromRoute)) {
          this.snackBar.open('Invalid submission ID.', 'Close', {
            duration: 3000,
          });
          this.loading.set(false);
          return;
        }
        this.loadSubmissionDetails(idFromRoute);
      });
    }
  }

  private loadSubmissionDetails(id: number): void {
    this.loading.set(true);
    this.submissionService
      .getSubmissionDetails(id)
      .pipe(
        catchError((err) => {
          this.snackBar.open('Failed to load submission details.', 'Close', {
            duration: 3000,
          });
          this.loading.set(false);
          return of(null);
        })
      )
      .subscribe((data) => {
        this.submissionData.set(data);
        this.loading.set(false);
      });
  }

  isUrl(url: string): boolean {
    if (!url) {
      return false;
    }
    try {
      new URL(url);
      return true;
    } catch (e) {
      return false;
    }
  }

  viewPdfProof(fileId: string): void {
    this.snackBar.open('Loading document...', 'Dismiss', { duration: 2000 });
    this.fileService.getFile(fileId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => {
        this.snackBar.open('Failed to load PDF proof.', 'Close', {
          duration: 3000,
        });
        console.error('Error fetching file:', err);
      },
    });
  }

  getTagProgressPercent(tag: SubmissionTagSnapshotResponseDto): number {
    if (tag.criteriaType === 'BOOLEAN') {
      return tag.submittedValue === 1 ? 100 : 0;
    }
    return tag.requiredValue > 0
      ? (tag.submittedValue / tag.requiredValue) * 100
      : 0;
  }
}
