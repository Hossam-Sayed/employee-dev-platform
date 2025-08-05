import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { MatDialogRef, MatDialogContent } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule, MatSelectionList } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { Tag } from '../models/tag.model';
import { TagService } from '../services/tag.service';
import { MatDialogActions } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatRadioModule } from '@angular/material/radio';

@Component({
  selector: 'app-tag-filter-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDialogContent,
    MatDialogActions,
    MatRadioModule,
  ],
  templateUrl: './tag-filter-dialog.component.html',
  styleUrl: './tag-filter-dialog.component.css',
})
export class TagFilterDialogComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<TagFilterDialogComponent>);
  private tagsService = inject(TagService);

  @ViewChild('tagList') tagList!: MatSelectionList;

  allTags = signal<Tag[]>([]);
  filteredTags = signal<Tag[]>([]);
  selectedTagId = signal<number | null>(null);
  isLoading = signal<boolean>(false);
  searchQuery = '';

  ngOnInit(): void {
    this.isLoading.set(true);
    this.tagsService.getAllActiveTags().subscribe({
      next: (tags) => {
        this.allTags.set(tags);
        this.filteredTags.set(tags);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch tags', err);
        this.isLoading.set(false);
      },
    });
  }

  filterTags(): void {
    const query = this.searchQuery.toLowerCase().trim();
    if (query) {
      this.filteredTags.set(
        this.allTags().filter((tag) => tag.name.toLowerCase().includes(query))
      );
    } else {
      this.filteredTags.set(this.allTags());
    }
  }

  onSelectionChange(): void {
    // The selection list handles the selected state internally. We'll grab the values on apply.
  }

  onClear(): void {
    this.selectedTagId.set(null);
    this.dialogRef.close(null);
  }

  onApply(): void {
    const selectedId = this.selectedTagId();
    this.dialogRef.close(selectedId);
  }
}
