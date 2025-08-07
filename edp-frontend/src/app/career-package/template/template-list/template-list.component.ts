import {
  Component,
  inject,
  signal,
  OnInit,
  computed,
  DestroyRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { TemplateService } from '../services/template.service';
import { TemplateResponseDto } from '../models/template-response.dto';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject, Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TemplateRequestDto } from '../models/template-request.dto';

@Component({
  selector: 'app-template-list',
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
  ],
  templateUrl: './template-list.component.html',
  styleUrls: ['./template-list.component.css'],
})
export class TemplateListComponent implements OnInit {
  private templateService = inject(TemplateService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private snackbar = inject(MatSnackBar);

  department = signal('');
  position = signal('');

  page = signal(0);
  size = 5;

  isLoading = signal(false);
  error = signal('');

  templates = signal<TemplateResponseDto[]>([]);
  totalPages = signal(0);

  isDeleting = signal(false);

  readonly pages = computed(() =>
    Array.from({ length: this.totalPages() }, (_, i) => i)
  );
  readonly isEmpty = computed(
    () => this.templates().length === 0 && !this.isLoading()
  );

  private filterdelay$ = new Subject<void>();
  private sub?: Subscription;

  get departmentModel() {
    return this.department();
  }
  set departmentModel(value: string) {
    this.department.set(value);
    this.page.set(0);
    this.emitFilter();
  }

  get positionModel() {
    return this.position();
  }
  set positionModel(value: string) {
    this.position.set(value);
    this.page.set(0);
    this.emitFilter();
  }

  ngOnInit(): void {
    this.sub = this.filterdelay$.pipe(debounceTime(700)).subscribe({
      next: (res) => {
        this.loadTemplates();
      },
    });

    this.emitFilter();

    this.destroyRef.onDestroy(() => {
      this.sub?.unsubscribe();
    });
  }

  emitFilter() {
    this.filterdelay$.next();
  }

  loadTemplates() {
    this.isLoading.set(true);
    this.error.set('');
    this.templateService
      .getTemplates(this.department(), this.position(), this.page(), this.size)
      .subscribe({
        next: (res) => {
          this.templates.set(res.content);
          this.totalPages.set(res.totalPages);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.error.set(err?.error.message || 'Error loading templates');
          this.isLoading.set(false);
        },
      });
  }

  deleteTemplate(id: number) {
    const confirmed = confirm('Are you sure you want to delete this template?');
    if (!confirmed) return;

    this.isDeleting.set(true);
    this.templateService.deleteTemplate(id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.templates.update((list) => list.filter((t) => t.id !== id));
        this.snackbar.open('Template deleted successfully', 'Close', {
          duration: 3000,
        });
      },
      error: (err) => {
        this.isDeleting.set(false);
        this.snackbar.open(
          'Error deleting template: ' + err.error.message,
          'Close',
          {
            duration: 3000,
          }
        );
      },
    });
  }

  createTemplate() {
    const request: TemplateRequestDto = {
      department: this.department(),
      position: this.position(),
    };

    this.isLoading.set(true);
    this.templateService.createTemplate(request).subscribe({
      next: (res) => {
        this.snackbar.open('Template created successfully!', 'Close', {
          duration: 3000,
        });
        this.loadTemplates();
        this.isLoading.set(false);
      },
      error: (err) => {
        this.snackbar.open(
          'Failed to create template: ' + err.error.message,
          'Close',
          {
            duration: 3000,
          }
        );
        this.isLoading.set(false);
      },
    });
  }

  goToEdit(id: number) {
    this.router.navigate(['/templates', id]);
  }

  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.set(this.page() + 1);
    }
    this.loadTemplates();
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.set(this.page() - 1);
    }
    this.loadTemplates();
  }

  goToPage(i: number) {
    if (i !== this.page()) {
      this.page.set(i);
      this.loadTemplates();
    }
  }
}
