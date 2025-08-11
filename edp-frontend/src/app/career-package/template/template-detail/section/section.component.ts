import { Component, inject, input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SectionRequiredTagService } from '../../services/section-required-tag.service';
import { TemplateDetailService } from '../../services/template-detail.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TemplateSectionService } from '../../services/template-section.service';
import { MatDialog } from '@angular/material/dialog';
import { AddTagDialogComponent } from './add-tag-dialog/add-tag-dialog.component';
import { TemplateSectionRequiredTagRequestDto } from '../../models/template-section-required-tag-request.dto';
import { TagService } from '../../services/tag.service';
import { TagRequestDto } from '../../models/tag-request.dto';
import { CriteriaType } from '../../models/criteria-type.enum';

@Component({
  selector: 'app-section',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.css'],
})
export class SectionComponent implements OnInit{
  sectionData = input<any>();
  sectionOrder = input<number>();
  displayedColumns = ['tagName', 'criteriaType', 'criteriaMinValue', 'actions'];
  private sectionRequiredTagService = inject(SectionRequiredTagService);
  private templateDetailService = inject(TemplateDetailService);
  private templateSectionService = inject(TemplateSectionService);
  private tagService = inject(TagService);
  private snackbar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  ngOnInit() {
    this.templateSectionService.listRequiredTags(this.sectionData().id).subscribe({
      next: (tags) => {
        this.templateDetailService.updateTags(this.sectionData().id, tags);
      },
      error: (err) => {
        this.snackbar.open(
          'Could not load required tags: ' + err.error.message,
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  removeTag(tag: any, sectionId: number) {
    if (!confirm(`Are you sure you want to delete tag "${tag.tagName}"?`))
      return;
    this.sectionRequiredTagService.detachRequiredTag(tag.id).subscribe({
      next: () => {
        this.templateDetailService.removeTag(sectionId, tag.id);
        this.snackbar.open('Tag deleted successfully', 'Close', {
          duration: 3000,
        });
      },
      error: (err) => {
        this.snackbar.open(
          'Could not delete tag: ' + err.error.message,
          'Close',
          {
            duration: 3000,
          }
        );
      },
    });
  }

  deleteSection(sectionId: number) {
    if (!confirm(`Are you sure you want to delete this section?`)) return;

    this.templateSectionService.deleteSection(sectionId).subscribe({
      next: () => {
        this.templateDetailService.removeSection(sectionId);
        this.snackbar.open('Section deleted successfully', 'Close', {
          duration: 3000,
        });
      },
      error: (err) => {
        this.snackbar.open(
          'Could not delete section: ' + err.error.message,
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  openAddTagDialog(section: any) {
    const ref = this.dialog.open(AddTagDialogComponent, {
      width: '500px',
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      if (result.create) {
        const createTagDto: TagRequestDto = { name: result.tag.name };

        this.tagService.createTag(createTagDto).subscribe({
          next: (createdTag) => {
            this.attachTagToSection(
              section.id,
              createdTag.id,
              result.criteriaType,
              result.criteriaMinValue,
              'Tag created and attached successfully'
            );
          },
          error: (err) => {
            this.snackbar.open(
              'Failed to create tag: ' + err.error.message,
              'Close',
              { duration: 3000 }
            );
          },
        });
      } else {
        this.attachTagToSection(
          section.id,
          result.tag.id,
          result.criteriaType,
          result.criteriaMinValue,
          'Tag attached successfully'
        );
      }
    });
  }
  private attachTagToSection(
    sectionId: number,
    tagId: number,
    criteriaType: CriteriaType,
    criteriaMinValue: number,
    successMessage: string
  ) {
    const attachDto: TemplateSectionRequiredTagRequestDto = {
      templateSectionId: sectionId,
      tagId,
      criteriaType,
      criteriaMinValue,
    };

    this.sectionRequiredTagService.attachRequiredTag(attachDto).subscribe({
      next: (attached) => {
        this.templateDetailService.addTag(sectionId, attached);
        this.snackbar.open(successMessage, 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.snackbar.open(
          'Failed to attach tag: ' + err.error.message,
          'Close',
          { duration: 3000 }
        );
      },
    });
  }
}
