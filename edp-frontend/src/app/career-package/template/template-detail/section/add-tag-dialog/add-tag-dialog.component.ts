import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { debounceTime, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { CriteriaType } from '../../../models/criteria-type.enum';
import { TagService } from '../../../services/tag.service';

@Component({
  selector: 'app-add-tag-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './add-tag-dialog.component.html'
})
export class AddTagDialogComponent {
  private dialogRef = inject(MatDialogRef<AddTagDialogComponent>);
  private tagService = inject(TagService);

  searchControl = new FormControl('');
  results = signal<any[]>([]);
  loading = signal(false);

  creatingNew = signal(false);
  selectedTag: any = null;

  criteriaType = signal<CriteriaType>(CriteriaType.HOURS);
  criteriaMinValue = signal<number>(1);

  CriteriaTypeEnum = CriteriaType; 

  ngOnInit() {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(700),
        switchMap(query => {
          if (!query || !query.trim()) {
            this.results.set([]);
            return of([]);
          }
          this.loading.set(true);
          return this.tagService.searchTags(query.trim());
        })
      )
      .subscribe(tags => {
        this.loading.set(false);
        this.results.set(tags);
      });
  }

  selectTag(tag: any) {
    this.selectedTag = tag;
    this.creatingNew.set(false);
  }

  startCreatingNew(name: string) {
    this.selectedTag = { name };
    this.creatingNew.set(true);
  }

  save() {
    if (!this.selectedTag) return;

    this.dialogRef.close({
      create: this.creatingNew(),
      tag: this.selectedTag,
      criteriaType: this.criteriaType(),
      criteriaMinValue: this.criteriaMinValue()
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
