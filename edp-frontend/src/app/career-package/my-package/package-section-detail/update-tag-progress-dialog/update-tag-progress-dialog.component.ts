import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TagProgressResponseDto } from '../../models/tag-progress-reponse.dto';

@Component({
  selector: 'app-update-tag-progress-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: './update-tag-progress-dialog.component.html'
})
export class UpdateTagProgressDialogComponent {
  private dialogRef = inject(MatDialogRef<UpdateTagProgressDialogComponent>);
  public data: TagProgressResponseDto = inject(MAT_DIALOG_DATA);

  completedValue = signal(this.data.completedValue);
  proofUrl = signal(this.data.proofUrl || '');
  file = signal<File | null>(null);

  isSaving = signal(false);

  fileName = signal(this.data.fileId ? 'Existing File' : 'No file selected');

  onFileSelected(event: Event) {
    const element = event.target as HTMLInputElement;
    const files = element.files;
    if (files && files.length > 0) {
      this.file.set(files[0]);
      this.fileName.set(files[0].name);
    }
  }

  save() {
    this.isSaving.set(true);
    if(this.data.criteriaType=='BOOLEAN'){
      if(this.proofUrl()||this.file()){
        this.completedValue.set(1);
      }
    }
    const result = {
      completedValue: this.completedValue(),
      proofUrl: this.proofUrl(),
      file: this.file(),
    };
    this.dialogRef.close(result);
  }

  cancel() {
    this.dialogRef.close(null);
  }
}