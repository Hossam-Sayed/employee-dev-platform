import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { debounceTime, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { SectionService } from '../../services/section.service';

@Component({
  selector: 'app-add-section-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatButtonModule
  ],
  templateUrl: './add-section-dialog.component.html'
})
export class AddSectionDialogComponent {

  private dialogRef = inject(MatDialogRef<AddSectionDialogComponent>);
  private sectionService = inject(SectionService);

  searchControl = new FormControl('');
  results = signal<any[]>([]);
  loading = signal(false);

  creatingNew = signal(false);
  newSectionDescription = signal('');

  selectedSection: any = null;

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
          return this.sectionService.searchSections(query.trim());
        })
      )
      .subscribe(sections => {
        this.loading.set(false);
        this.results.set(sections);
      });
  }

  selectSection(section: any) {
    this.selectedSection = section;
    this.creatingNew.set(false);
  }

  startCreatingNew(name: string) {
    this.selectedSection = { name };
    this.creatingNew.set(true);
  }

  save() {
    if (this.creatingNew()) {
      this.dialogRef.close({
        create: true,
        name: this.selectedSection.name,
        description: this.newSectionDescription()
      });
    } else {
      this.dialogRef.close({
        create: false,
        section: this.selectedSection
      });
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
