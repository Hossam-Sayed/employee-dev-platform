import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormArray,
  FormControl,
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
import {
  Observable,
  switchMap,
  tap,
  of,
  catchError,
  forkJoin,
  EMPTY,
} from 'rxjs';
import { LibraryService } from '../services/library.service';
import { TagService } from '../services/tag.service';
import { FileService } from '../services/file.service';
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
  private fileService = inject(FileService);

  materialType = signal<MaterialType | null>(null);
  materialId = signal<number | null>(null);
  form!: FormGroup;
  allTags = signal<Tag[]>([]);
  isLoading = signal(true);
  isSubmitting = signal(false);
  isEditMode = signal(false);

  // Signals to manage the file for blogs and wikis
  selectedFile = signal<File | null>(null);
  currentFileName = signal<string | null>(null);
  fileControl = new FormControl<File | null>(null);

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
            // In edit mode, the file is not required on load
            this.fileControl.clearValidators();
          } else {
            // In add mode, the file is required for blogs/wikis
            this.fileControl.setValidators(Validators.required);
          }
        }),
        switchMap(() => {
          this.initializeForm();
          const tagFetch$ = this.tagsService.getAllActiveTags();

          let materialFetch$: Observable<MaterialResponse | null>;
          if (this.isEditMode() && this.materialId()) {
            materialFetch$ = this.getMaterialDetails(
              this.materialId()!,
              this.materialType()!
            ).pipe(
              // After fetching material, fetch the file if it's a blog or wiki
              switchMap((material) => {
                if (
                  material &&
                  (this.materialType() === 'blog' ||
                    this.materialType() === 'wiki')
                ) {
                  // Assuming the material response has a documentId
                  const documentId =
                    'documentId' in material ? material.documentId : null;
                  if (documentId) {
                    // Fetch the file and combine it with the material data
                    return this.fileService.getFile(documentId).pipe(
                      tap((blob) => {
                        // Create a file from the blob to pre-populate the form
                        const fileName = 'document.pdf'; // Use a default name, or fetch from a 'documentName' field if your API provides it
                        const file = new File([blob], fileName, {
                          type: blob.type,
                        });
                        this.selectedFile.set(file);
                        this.fileControl.setValue(file);
                        this.currentFileName.set(fileName);
                      }),
                      switchMap(() => of(material))
                    );
                  }
                }
                return of(material);
              }),
              catchError((err) => {
                console.error('Failed to fetch material or file', err);
                return of(null);
              })
            );
          } else {
            materialFetch$ = of(null);
          }

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
              return EMPTY;
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
      // Add the file control to the form group for validation
      this.form.addControl('file', this.fileControl);
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
      });
      this.populateGenericTags(
        material.tags as (BlogTagResponse | WikiTagResponse)[]
      );
    }
  }

  private populateLearningTags(tags: LearningTagResponse[]): void {
    this.tagsArray.clear();
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

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);
      this.fileControl.setValue(file);
      this.currentFileName.set(file.name);
    }
  }

  onSubmit(): void {
    if (
      this.form.invalid ||
      (this.materialType() !== 'learning' && this.fileControl.invalid)
    ) {
      this.form.markAllAsTouched();
      this.fileControl.markAsTouched();
      return;
    }
    this.isSubmitting.set(true);

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
            learningEditRequest
          );
          break;
        case 'blog':
          const blogEditRequest: BlogCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          // Pass the selected file to the resubmit method
          actionObservable = this.libraryService.resubmitBlog(
            this.materialId()!,
            blogEditRequest,
            this.selectedFile()
          );
          break;
        case 'wiki':
          const wikiEditRequest: WikiCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          // Pass the selected file to the resubmit method
          actionObservable = this.libraryService.resubmitWiki(
            this.materialId()!,
            wikiEditRequest,
            this.selectedFile()
          );
          break;
        default:
          console.error('Unknown material type');
          this.isSubmitting.set(false);
          return;
      }
    } else {
      // Create logic remains the same
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
            learningCreateRequest
          );
          break;
        case 'blog':
          const blogCreateRequest: BlogCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.createBlog(
            blogCreateRequest,
            this.selectedFile()!
          );
          break;
        case 'wiki':
          const wikiCreateRequest: WikiCreateRequest = {
            title: formValue.title,
            description: formValue.description,
            tagIds: formValue.tags.map((t: any) => t.tagId),
          };
          actionObservable = this.libraryService.createWiki(
            wikiCreateRequest,
            this.selectedFile()!
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
