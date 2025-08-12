import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {
  MatDialogRef,
  MatDialogActions,
  MatDialogContent,
  MatDialogTitle,
} from '@angular/material/dialog';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-tag-reject-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  templateUrl: './tag-reject-dialog.component.html',
  styleUrls: ['./tag-reject-dialog.component.css'],
})
export class TagRejectDialogComponent {
  private dialogRef: MatDialogRef<TagRejectDialogComponent> =
    inject(MatDialogRef);

  commentControl = new FormControl('', [
    Validators.required,
    Validators.minLength(10),
  ]);

  onReject(): void {
    if (this.commentControl.valid) {
      this.dialogRef.close(this.commentControl.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
