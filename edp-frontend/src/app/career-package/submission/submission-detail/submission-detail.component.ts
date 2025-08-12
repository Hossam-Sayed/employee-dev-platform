import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
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
import { SubmissionService } from '../../my-package/services/submission-service';
import { FileService } from '../../my-package/services/file.service';
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
    DatePipe 
  ],
  templateUrl: './submission-detail.component.html',
  styleUrls: ['./submission-detail.component.css']
})
export class SubmissionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private submissionDetailService = inject(SubmissionService);
  private fileService = inject(FileService);
  private snackBar = inject(MatSnackBar);

  loading = signal(true);
  submissionData = signal<SubmissionSnapshotResponseDto | null>(null);

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const submissionId = Number(params.get('submissionId'));
        if (isNaN(submissionId)) {
          this.snackBar.open('Invalid submission ID.', 'Close', { duration: 3000 });
          this.loading.set(false);
          return of(null);
        }

        this.loading.set(true);
        return this.submissionDetailService.getSubmissionDetails(submissionId).pipe(
          catchError(err => {
            this.snackBar.open('Failed to load submission details: '+err.error.message, 'Close', { duration: 3000 });
            return of(null);
          })
        );
      })
    ).subscribe(data => {
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
        this.snackBar.open('Failed to load PDF proof.', 'Close', { duration: 3000 });
        console.error('Error fetching file:', err);
      }
    });
  }


  getTagProgressPercent(tag: SubmissionTagSnapshotResponseDto): number {
    if (tag.criteriaType === 'BOOLEAN') {
      return tag.submittedValue === 1 ? 100 : 0;
    }
    return tag.requiredValue > 0 ? (tag.submittedValue / tag.requiredValue) * 100 : 0;
  }
}