import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PackageService } from '../services/career-package.service';
import { PackageDetailService } from '../services/package-store.service';
import { SubmissionService } from '../services/submission-service';
import { PackageSectionComponent } from "./package-section/package-section.component";
import { PackageSubmissionComponent } from "./package-submission/package-submission.component";

@Component({
  selector: 'app-package-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatTableModule,
    MatExpansionModule,
    MatDialogModule,
    MatAccordion,
    PackageSectionComponent,
    PackageSubmissionComponent
],
  templateUrl: './package-detail.component.html',
  styleUrls: ['./package-detail.component.css'],
})
export class PackageDetailComponent implements OnInit {
  private snackBar = inject(MatSnackBar);
  private packageService = inject(PackageService);
  private packageStoreService = inject(PackageDetailService);
  private submissionService = inject(SubmissionService);


  loading = signal(true);
  careerPackage = this.packageStoreService.package;

  ngOnInit(): void {
    this.packageService.getCareerPackage().subscribe({
      next: (data) => {
        this.packageStoreService.setCareerPackage(data);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Failed to load career package.', 'Close', {
          duration: 3000,
        });
        this.loading.set(false);
      },
    });
  }

    submitForReview(): void {
    if (this.careerPackage()?.status === 'COMPLETED') {
      this.submissionService.submitCareerPackage().subscribe({
        next: () => {
          this.snackBar.open('Career package submitted for review.', 'Close', {
            duration: 3000,
          });
          this.packageStoreService.submitCareerPackage();
        },
        error: () => {
          this.snackBar.open('Submission failed.', 'Close', {
            duration: 3000,
          });
        }
      });
    } else {
      this.snackBar.open('Submission not allowed. Package is not completed.', 'Close', {
        duration: 3000,
      });
    }
  }

}
