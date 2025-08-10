import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { finalize } from 'rxjs/operators';
import { Tag } from '../models/tag.model';
import { TagService } from '../services/tag.service';
import { AuthService } from '../../auth/service/auth.service';
import { TagCreateRequest } from '../models/tag-create-request.model';

@Component({
  selector: 'app-tag-request-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
  ],
  templateUrl: './tag-request-dialog.component.html',
  styleUrls: ['./tag-request-dialog.component.css'],
})
export class TagRequestDialogComponent {
  private dialogRef = inject(MatDialogRef<TagRequestDialogComponent>);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private tagService = inject(TagService);
  private tagsService = inject(TagService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  isAdmin = this.authService.isAdmin();

  tagRequestForm = this.fb.nonNullable.group({
    requestedName: [
      '',
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.tagRequestForm.invalid) {
      return;
    }

    const requestedName = this.tagRequestForm.value.requestedName!.trim();

    // Check against existing tags locally first (optional but good for UX)
    this.tagsService.getAllActiveTags().subscribe((existingTags) => {
      if (
        existingTags.some(
          (tag: Tag) => tag.name.toLowerCase() === requestedName.toLowerCase()
        )
      ) {
        this.errorMessage.set(`A tag named "${requestedName}" already exists.`);
        return;
      }

      this.isLoading.set(true);
      this.errorMessage.set(null);

      const requestBody: TagCreateRequest = { requestedName };

      if (this.isAdmin) {
        this.tagService
          .createTagRequest(requestBody)
          .pipe(finalize(() => this.isLoading.set(false)))
          .subscribe({
            next: (response) => {
              this.dialogRef.close({
                success: true,
                message: `Tag request "${response.requestedName}" submitted successfully!`,
              });
            },
            error: (err) => {
              if (err.status === 409) {
                this.errorMessage.set(
                  err.error?.message ||
                    'A tag with this name already exists or has a pending request.'
                );
              } else {
                this.errorMessage.set(
                  'Failed to submit tag request. Please try again.'
                );
              }
            },
          });
      } else {
        this.tagService
          .createTagRequest(requestBody)
          .pipe(finalize(() => this.isLoading.set(false)))
          .subscribe({
            next: (response) => {
              this.dialogRef.close({
                success: true,
                message: `Tag request "${response.requestedName}" submitted successfully!`,
              });
            },
            error: (err) => {
              if (err.status === 409) {
                this.errorMessage.set(
                  err.error?.message ||
                    'A tag with this name already exists or has a pending request.'
                );
              } else {
                this.errorMessage.set(
                  'Failed to submit tag request. Please try again.'
                );
              }
            },
          });
      }
    });
  }
}
