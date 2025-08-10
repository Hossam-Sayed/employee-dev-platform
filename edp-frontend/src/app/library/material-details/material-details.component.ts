import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { EMPTY, switchMap, catchError, Observable, tap, of, pipe } from 'rxjs';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';

import { LibraryService } from '../services/library.service';
import { FileService } from '../services/file.service';
import { LearningResponse } from '../models/learning-response.model';
import { BlogResponse } from '../models/blog-response.model';
import { WikiResponse } from '../models/wiki-response.model';
import { AuthService } from '../../auth/service/auth.service';
import { LearningTagResponse } from '../models/learning-tag-response.model';
import { MaterialType } from '../models/material.type';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { MaterialResponse } from '../models/material-response.type';
import { SubmissionStatus } from '../models/submission-status.model';
import { ReviewConfirmationDialogComponent } from '../review-confirmation-dialog/review-confirmation-dialog.component';
import { BlogSubmissionResponse } from '../models/blog-submission-response.model';
import { LearningSubmissionResponse } from '../models/learning-submission-response.model';
import { WikiSubmissionResponse } from '../models/wiki-submission-response.model';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

type SubmissionResponse =
  | LearningSubmissionResponse
  | BlogSubmissionResponse
  | WikiSubmissionResponse;

@Component({
  selector: 'app-material-details',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    RouterLink,
    CustomTagComponent,
    MatDialogModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonToggleModule,
    FormsModule,
  ],
  templateUrl: './material-details.component.html',
  styleUrls: ['./material-details.component.css'],
})
export class MaterialDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private libraryService = inject(LibraryService);
  private authService = inject(AuthService);
  private fileService = inject(FileService);
  private sanitizer = inject(DomSanitizer);
  private dialog = inject(MatDialog);

  material = signal<MaterialResponse | null>(null);
  materialType = signal<MaterialType | null>(null);
  isOwner = signal(false);
  isManager = signal(false);
  isPending = signal(false);
  isRejected = signal(false);
  isLoading = signal(true);
  error = signal<string | null>(null);
  fileDataUrl = signal<SafeResourceUrl | null>(null);

  showRejectionForm = signal(false);
  rejectionComment = signal<string>('');
  selectedReviewAction = signal<'APPROVED' | 'REJECTED' | null>(null);

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const materialId = Number(params.get('materialId'));
          const type = this.route.snapshot.url[0]?.path as MaterialType;

          if (!materialId || !type) {
            this.error.set('Invalid URL. Missing material type or ID.');
            return EMPTY;
          }

          this.materialType.set(type);
          this.isLoading.set(true);
          this.fileDataUrl.set(null);
          this.showRejectionForm.set(false); // Reset form state on navigation

          let details$: Observable<MaterialResponse>;
          switch (type) {
            case 'learning':
              details$ = this.libraryService.getLearningDetails(materialId);
              break;
            case 'blog':
              details$ = this.libraryService.getBlogDetails(materialId);
              break;
            case 'wiki':
              details$ = this.libraryService.getWikiDetails(materialId);
              break;
            default:
              this.error.set('Unsupported material type.');
              return EMPTY;
          }

          return details$.pipe(
            switchMap((material) => {
              this.material.set(material);
              this.checkUserPermissions(material);
              this.isPending.set(material.status === 'PENDING');

              if (
                !this.isLearning(material) &&
                'documentId' in material &&
                material.documentId
              ) {
                return this.fileService.getFile(material.documentId).pipe(
                  tap((blob) => {
                    const url = URL.createObjectURL(blob);
                    this.fileDataUrl.set(
                      this.sanitizer.bypassSecurityTrustResourceUrl(url)
                    );
                  }),
                  switchMap(() => of(material))
                );
              }
              return of(material);
            }),
            catchError((err) => {
              this.error.set('Failed to fetch material details or file.');
              console.error('Error fetching material details or file:', err);
              this.isLoading.set(false);
              return EMPTY;
            })
          );
        })
      )
      .subscribe(() => {
        this.isLoading.set(false);
      });
  }

  private checkUserPermissions(material: MaterialResponse): void {
    const userId = this.authService.getUserId();

    // TODO: Edit logic for these checks
    const userRole = this.authService.getUserRole();
    const managerId = this.authService.getUserId();

    if (userId) {
      const ownerId =
        'employeeId' in material ? material.employeeId : material.authorId;
      this.isOwner.set(userId === ownerId);
      this.isRejected.set(material.status === 'REJECTED');
      // Set isManager to true if the current user is a manager for the material owner
      this.isManager.set(userId === managerId && userRole === 'MANAGER');
    }
  }

  onApprove(): void {
    this.openConfirmationDialog(
      'APPROVED',
      'Are you sure you want to approve this submission?'
    );
  }

  onReject(): void {
    this.showRejectionForm.set(true);
  }

  onSubmitReview(status: SubmissionStatus): void {
    const isRejection = status === 'REJECTED';
    const message = isRejection
      ? 'Are you sure you want to reject this submission?'
      : 'Are you sure you want to approve this submission?';

    this.openConfirmationDialog(status, message);
  }

  private openConfirmationDialog(
    status: SubmissionStatus,
    message: string
  ): void {
    const dialogRef = this.dialog.open(ReviewConfirmationDialogComponent, {
      data: {
        message,
        title: `${status === 'APPROVED' ? 'Approve' : 'Reject'} Submission`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User confirmed, proceed with the review action
        const reviewRequest = {
          status: status,
          reviewerComment:
            status === 'REJECTED' ? this.rejectionComment() : null,
        };

        let reviewRequest$: Observable<SubmissionResponse>;

        switch (this.materialType()) {
          case 'learning':
            reviewRequest$ = this.libraryService.reviewLearningSubmission(
              this.material()?.currentSubmissionId!,
              reviewRequest
            );
            break;
          case 'blog':
            reviewRequest$ = this.libraryService.reviewBlogSubmission(
              this.material()?.currentSubmissionId!,
              reviewRequest
            );
            break;
          case 'wiki':
            reviewRequest$ = this.libraryService.reviewWikiSubmission(
              this.material()?.currentSubmissionId!,
              reviewRequest
            );
            break;
          default:
            this.error.set('Unsupported material type.');
            return;
        }

        reviewRequest$
          .pipe(
            tap(() => {
              this.router.navigate(['/library/review-pending'], {
                replaceUrl: true,
              });
            }),
            catchError((err) => {
              console.error('Failed to review submission:', err);
              // Handle error gracefully, maybe show a snackbar
              return EMPTY;
            })
          )
          .subscribe();
      }
    });
  }

  get learningMaterial(): LearningResponse | null {
    return this.isLearning(this.material())
      ? (this.material() as LearningResponse)
      : null;
  }

  get blogOrWikiMaterial(): BlogResponse | WikiResponse | null {
    return !this.isLearning(this.material())
      ? (this.material() as BlogResponse | WikiResponse)
      : null;
  }

  get learningTags(): LearningTagResponse[] {
    return this.learningMaterial?.tags ?? [];
  }

  isLearning(material: MaterialResponse | null): material is LearningResponse {
    return !!material && 'proofUrl' in material;
  }

  onCommentChange(event: any): void {
    this.rejectionComment.set(event.target.value);
  }
}
