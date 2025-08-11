import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TemplateService } from '../services/template.service';
import { TemplateDetailResponseDto } from '../models/template-detail-response.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SectionComponent } from './section/section.component';
import { MatTableModule } from '@angular/material/table';
import { TemplateDetailService } from '../services/template-store.service';
import { AddSectionDialogComponent } from './add-section-dialog/add-section-dialog.component';
import { SectionRequestDto } from '../models/section-request.dto';
import { SectionService } from '../services/section.service';
import { TemplateSectionService } from '../services/template-section.service';
import { TemplateSectionRequestDto } from '../models/template-section-request.dto';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-template-detail',
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
    MatTableModule,
    MatExpansionModule,
    MatDialogModule,
    SectionComponent,
  ],
  templateUrl: './template-detail.component.html',
  styleUrls: ['./template-detail.component.css'],
})
export class TemplateDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private templateService = inject(TemplateService);
  templateDetailService = inject(TemplateDetailService);
  private sectionService = inject(SectionService);
  private templateSectionService = inject(TemplateSectionService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  private originalDepartment = signal<string>('');
  private originalPosition = signal<string>('');

  isDirty = computed(() => {
    const t = this.templateDetailService.template();
    if (!t) return false;
    return (
      t.department !== this.originalDepartment() ||
      t.position !== this.originalPosition()
    );
  });

  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id')!;
    this.templateService.getTemplateById(id).subscribe((data) => {
      this.templateDetailService.setTemplate(data);
      this.originalDepartment.set(data.department);
      this.originalPosition.set(data.position);
    });
  }

  saveTemplateMeta(): void {
    const t = this.templateDetailService.template();
    if (!t) return;

    const updateDto = {
      department: t.department,
      position: t.position,
    };

    this.templateService.updateTemplate(t.id, updateDto).subscribe({
      next: (updated) => {
        this.originalDepartment.set(updated.department);
        this.originalPosition.set(updated.position);
        
        this.snackBar.open('Template updated successfully', 'Close', {
          duration: 3000,
        });
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open(
          'Failed to update template: ' + err.error.message,
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  openAddSectionDialog() {
    const ref = this.dialog.open(AddSectionDialogComponent, { width: '500px' });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      if (result.create) {
        const createDto: SectionRequestDto = {
          name: result.name,
          description: result.description,
        };

        this.sectionService.createSection(createDto).subscribe({
          next: (section) =>
            this.attachSection(
              section.id,
              'Section created and attached successfully'
            ),
          error: (err) => {
            console.error(err);
            this.snackBar.open(
              'Failed to create section: ' + err.error.message,
              'Close',
              { duration: 3000 }
            );
          },
        });
      } else {
        this.attachSection(result.section.id, 'Section attached successfully');
      }
    });
  }

  private attachSection(sectionId: number, successMessage: string) {
    const attachDto: TemplateSectionRequestDto = {
      templateId: this.templateDetailService.template()?.id ?? 0,
      sectionId,
    };

    this.templateSectionService.attachSection(attachDto).subscribe({
      next: (res) => {
        this.snackBar.open(successMessage, 'Close', { duration: 3000 });
        this.templateDetailService.addSection(res);
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open(
          'Failed to attach section to template: ' + err.error.message,
          'Close',
          { duration: 3000 }
        );
      },
    });
  }
}
