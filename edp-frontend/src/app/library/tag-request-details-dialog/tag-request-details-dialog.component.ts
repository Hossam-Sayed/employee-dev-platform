import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatDividerModule } from '@angular/material/divider';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { TagRequestResponse } from '../models/tag-request-response.model';

@Component({
  selector: 'app-tag-request-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatToolbarModule,
    MatDividerModule,
    CustomTagComponent,
  ],
  templateUrl: './tag-request-details-dialog.component.html',
  styleUrls: ['./tag-request-details-dialog.component.css'],
})
export class TagRequestDetailsDialogComponent {
  private dialogRef = inject(MatDialogRef<TagRequestDetailsDialogComponent>);

  constructor(@Inject(MAT_DIALOG_DATA) public data: TagRequestResponse) {}

  onClose(): void {
    this.dialogRef.close();
  }
}
