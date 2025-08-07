import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormArray,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Observable, switchMap, tap, of, catchError, forkJoin } from 'rxjs';
import { LibraryService } from '../services/library.service';
import { TagService } from '../services/tag.service';
import { Tag } from '../models/tag.model';
import { BlogCreateRequest } from '../models/blog-create-request.model';
import { LearningCreateRequest } from '../models/learning-create-request.model';
import { WikiCreateRequest } from '../models/wiki-create-request.model';
import { MaterialType } from '../models/material.type';
import { BlogTagResponse } from '../models/blog-tag-response.model';
import { LearningTagResponse } from '../models/learning-tag-response.model';
import { WikiTagResponse } from '../models/wiki-tag-response.model';
import { MaterialResponse } from '../models/material-response.type';

@Component({
  selector: 'app-add-material',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    MatChipsModule,
    MatSelectModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './add-material.component.html',
  styleUrls: ['./add-material.component.css'],
})
export class AddMaterialComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private libraryService = inject(LibraryService);
  private tagsService = inject(TagService);

  materialType = signal<MaterialType | null>(null);
  materialId = signal<number | null>(null);
  form!: FormGroup;
  allTags = signal<Tag[]>([]);
  isLoading = signal(true);
  isSubmitting = signal(false);
  isEditMode = signal(false);

  ngOnInit(): void {
    // Check for material ID in the route to determine edit mode
    this.route.paramMap
      .pipe(
        tap((params) => {
          const type = this.route.snapshot.url[0]?.path
            .replace('add-', '')
            .replace('edit-', '') as MaterialType;
          this.materialType.set(type);
          const id = Number(params.get('materialId'));
          if (id) {
            this.materialId.set(id);
            this.isEditMode.set(true);
          }
        }),
        switchMap(() => {
          this.initializeForm();
          const tagFetch$ = this.tagsService.getAllActiveTags();
          const materialFetch$ =
            this.isEditMode() && this.materialId()
              ? this.getMaterialDetails(
                  this.materialId()!,
                  this.materialType()!
                )
              : of(null);

          return forkJoin({
            tags: tagFetch$,
            material: materialFetch$,
          }).pipe(
            tap(({ tags, material }) => {
              this.allTags.set(tags);
              if (material) {
                this.populateForm(material);
              }
              this.isLoading.set(false);
            }),
            catchError((err) => {
              console.error('Failed to fetch data', err);
              this.isLoading.set(false);
              return of({ tags: [], material: null });
            })
          );
        })
      )
      .subscribe();
  }

  private getMaterialDetails(
    id: number,
    type: MaterialType
  ): Observable<MaterialResponse> {
    switch (type) {
      case 'learning':
        return this.libraryService.getLearningDetails(id);
      case 'blog':
        return this.libraryService.getBlogDetails(id);
      case 'wiki':
        return this.libraryService.getWikiDetails(id);
      default:
        return of(null) as unknown as Observable<MaterialResponse>;
    }
  }

  initializeForm(): void {
    const isLearning = this.materialType() === 'learning';
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      tags: this.fb.array([], [Validators.required]),
    });

    if (isLearning) {
      this.form.addControl(
        'proofUrl',
        this.fb.control('', Validators.required)
      );
    } else {
      this.form.addControl(
        'description',
        this.fb.control('', Validators.required)
      );
      this.form.addControl(
        'documentUrl',
        this.fb.control('', Validators.required)
      );
    }
  }

  private populateForm(material: MaterialResponse): void {
    this.form.patchValue({
      title: material.title,
    });

    if ('proofUrl' in material) {
      this.form.patchValue({ proofUrl: material.proofUrl });
      this.populateLearningTags(material.tags as LearningTagResponse[]);
    } else {
      this.form.patchValue({
        description: material.description,
        documentUrl: material.documentUrl,
      });
      this.populateGenericTags(
        material.tags as (BlogTagResponse | WikiTagResponse)[]
      );
    }
  }

  private populateLearningTags(tags: LearningTagResponse[]): void {
    tags.forEach((tag) => {
      this.tagsArray.push(
        this.fb.group({
          tagId: [tag.id],
          tagName: [tag.tagName],
          durationMinutes: [
            tag.durationMinutes,
            [Validators.required, Validators.min(1)],
          ],
        })
      );
    });
  }

  private populateGenericTags(
    tags: (BlogTagResponse | WikiTagResponse)[]
  ): void {
    this.tagsArray.clear();
    tags.forEach((tag) => {
      this.tagsArray.push(
        this.fb.group({
          tagId: [tag.tagId],
          tagName: [tag.tagName],
        })
      );
    });
  }

  get tagsArray(): FormArray {
    return this.form.get('tags') as FormArray;
  }

  addTag(tag: Tag, event?: any): void {
    if (event) {
      event.preventDefault();
    }

    const isTagAlreadyAdded = this.tagsArray.controls.some(
      (control) => control.get('tagId')?.value === tag.id
    );

    if (!isTagAlreadyAdded) {
      const newTagControl = this.fb.group({
        tagId: [tag.id],
        tagName: [tag.name],
        durationMinutes: [
          this.materialType() === 'learning' ? null : undefined,
          this.materialType() === 'learning'
            ? [Validators.required, Validators.min(1)]
            : [],
        ],
      });

      this.tagsArray.push(newTagControl);
    }
  }

  removeTag(index: number): void {
    this.tagsArray.removeAt(index);
  }

  getFilteredTags(): Tag[] {
    const selectedTagIds = this.tagsArray.controls.map(
      (control) => control.get('tagId')?.value
    );
    return this.allTags().filter((tag) => !selectedTagIds.includes(tag.id));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isSubmitting.set(true);
    const reviewerId = 1; // TODO: Get this from a real service

    let actionObservable: Observable<any>;
    const formValue = this.form.value;

    if (this.isEditMode()) {
      switch (this.materialType()) {
        case 'learning':
          const learningEditRequest: LearningCreateRequest = {
            title: formValue.title,
            proofUrl: formValue.proofUrl,
            tags: formValue.tags.map((t: any) => ({
              tagId: t.tagId,
              durationMinutes: t.durationMinutes,
            })),
          };
          actionObservable = this.libraryService.resubmitLearning(
            this.materialId()!,
            learningEditRequest,
            reviewerId
          );
          break;
        case 'blog':
          const blogEditRequest: BlogCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            documentUrl: formValue.documentUrl,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.resubmitBlog(
            this.materialId()!,
            blogEditRequest,
            reviewerId
          );
          break;
        case 'wiki':
          const wikiEditRequest: WikiCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            documentUrl: formValue.documentUrl,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.resubmitWiki(
            this.materialId()!,
            wikiEditRequest,
            reviewerId
          );
          break;
        default:
          console.error('Unknown material type');
          this.isSubmitting.set(false);
          return;
      }
    } else {
      // This is the original 'create' logic
      switch (this.materialType()) {
        case 'learning':
          const learningCreateRequest: LearningCreateRequest = {
            title: formValue.title,
            proofUrl: formValue.proofUrl,
            tags: formValue.tags.map((t: any) => ({
              tagId: t.tagId,
              durationMinutes: t.durationMinutes,
            })),
          };
          actionObservable = this.libraryService.createLearning(
            learningCreateRequest,
            reviewerId
          );
          break;
        case 'blog':
          const blogCreateRequest: BlogCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            documentUrl: formValue.documentUrl,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.createBlog(
            blogCreateRequest,
            reviewerId
          );
          break;
        case 'wiki':
          const wikiCreateRequest: WikiCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            documentUrl: formValue.documentUrl,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.createWiki(
            wikiCreateRequest,
            reviewerId
          );
          break;
        default:
          console.error('Unknown material type');
          this.isSubmitting.set(false);
          return;
      }
    }

    actionObservable
      .pipe(
        tap(() => {
          this.isSubmitting.set(false);
          console.log(
            `Material ${
              this.isEditMode() ? 'resubmitted' : 'created'
            } successfully`
          );
          this.router.navigate(['/library/my-materials'], { replaceUrl: true });
        }),
        catchError((err) => {
          console.error(
            `Error ${
              this.isEditMode() ? 'resubmitting' : 'creating'
            } material:`,
            err
          );
          this.isSubmitting.set(false);
          return of(null);
        })
      )
      .subscribe();
  }
}
