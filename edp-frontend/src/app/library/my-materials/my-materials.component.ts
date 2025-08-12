import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort, SortDirection } from '@angular/material/sort';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { Observable } from 'rxjs';
import { BlogResponse } from '../models/blog-response.model';
import { LearningResponse } from '../models/learning-response.model';
import { PaginationRequest } from '../models/pagination-request.model';
import { PaginationResponse } from '../models/pagination-response.model';
import { SubmissionStatus } from '../models/submission-status.model';
import { Tag } from '../models/tag.model';
import { WikiResponse } from '../models/wiki-response.model';
import { LibraryService } from '../services/library.service';
import { TagService } from '../services/tag.service';
import { TagFilterDialogComponent } from '../tag-filter-dialog/tag-filter-dialog.component';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { MaterialResponse } from '../models/material-response.type';
import { MaterialType } from '../models/material.type';
import { AuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-my-materials',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatMenuModule,
    RouterModule,
    CustomTagComponent,
  ],
  templateUrl: './my-materials.component.html',
  styleUrls: ['./my-materials.component.css'],
})
export class MyMaterialsComponent implements OnInit {
  private libraryService = inject(LibraryService);
  private tagsService = inject(TagService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  currentTab = signal<MaterialType>('learning');

  // Now managing data for all three types
  learningsData = signal<PaginationResponse<LearningResponse> | null>(null);
  blogsData = signal<PaginationResponse<BlogResponse> | null>(null);
  wikisData = signal<PaginationResponse<WikiResponse> | null>(null);

  isLoading = signal<boolean>(false);
  allTags = signal<Tag[]>([]);
  isAdmin = this.authService.isAdmin();

  // Filtering and Pagination
  paginationRequest = signal<PaginationRequest>({
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortDirection: 'DESC',
  });
  statusFilter = signal<SubmissionStatus | null>(null);
  tagIdFilter = signal<number | null>(null);

  displayedColumns: string[] = [
    'title',
    'status',
    'tags',
    'createdAt',
    'updatedAt',
    // 'actions',
  ];

  readonly tabLabels = {
    learning: 'My Learnings',
    blog: 'My Blogs',
    wiki: 'My Wikis',
  };

  readonly addMaterialLabels = {
    learning: 'Add Learning',
    blog: 'Add Blog',
    wiki: 'Add Wiki',
  };

  readonly submissionStatuses: SubmissionStatus[] = [
    'PENDING',
    'APPROVED',
    'REJECTED',
  ];

  ngOnInit(): void {
    this.fetchMaterials();
    this.fetchTags();
  }

  onTabChange(index: number): void {
    const tabNames: MaterialType[] = ['learning', 'blog', 'wiki'];
    this.currentTab.set(tabNames[index]);
    this.resetFilters();
    this.fetchMaterials();
  }

  fetchMaterials(): void {
    this.isLoading.set(true);
    const request = this.paginationRequest();
    const status = this.statusFilter();
    const tagId = this.tagIdFilter();

    let fetchObservable: Observable<PaginationResponse<any>>;

    switch (this.currentTab()) {
      case 'learning':
        fetchObservable = this.libraryService.getMyLearnings(
          request,
          status,
          tagId
        );
        break;
      case 'blog':
        fetchObservable = this.libraryService.getMyBlogs(
          request,
          status!,
          tagId!
        );
        break;
      case 'wiki':
        fetchObservable = this.libraryService.getMyWikis(
          request,
          status!,
          tagId!
        );
        break;
      default:
        // This case should not be reached but is good practice
        this.isLoading.set(false);
        return;
    }

    fetchObservable.subscribe({
      next: (response) => {
        switch (this.currentTab()) {
          case 'learning':
            this.learningsData.set(
              response as PaginationResponse<LearningResponse>
            );
            break;
          case 'blog':
            this.blogsData.set(response as PaginationResponse<BlogResponse>);
            break;
          case 'wiki':
            this.wikisData.set(response as PaginationResponse<WikiResponse>);
            break;
        }
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(`Failed to fetch ${this.currentTab()} materials`, err);
        this.isLoading.set(false);
      },
    });
  }

  fetchTags(): void {
    this.tagsService.getAllActiveTags().subscribe({
      next: (tags) => this.allTags.set(tags),
      error: (err) => console.error('Failed to fetch tags', err),
    });
  }

  onPageChange(event: PageEvent): void {
    this.paginationRequest.update((req) => ({
      ...req,
      page: event.pageIndex,
      size: event.pageSize,
    }));
    this.fetchMaterials();
  }

  onSortChange(event: Sort): void {
    this.paginationRequest.update((req) => ({
      ...req,
      sortBy: event.active,
      sortDirection: event.direction.toUpperCase() as 'ASC' | 'DESC',
    }));
    this.fetchMaterials();
  }

  openTagFilterDialog(): void {
    const dialogRef = this.dialog.open(TagFilterDialogComponent, {
      width: '400px',
      data: { selectedTagId: this.tagIdFilter() }, // Pass the current selected tag ID to the dialog
    });

    dialogRef.afterClosed().subscribe((selectedTagId: number | null) => {
      if (selectedTagId !== undefined) {
        this.tagIdFilter.set(selectedTagId);
        this.fetchMaterials();
      }
    });
  }

  removeTagFilter(): void {
    this.tagIdFilter.set(null);
    this.fetchMaterials();
  }

  applyStatusFilter(status: SubmissionStatus | null): void {
    this.statusFilter.set(status);
    this.paginationRequest.update((req) => ({ ...req, page: 0 }));
    this.fetchMaterials();
  }

  resetFilters(): void {
    this.paginationRequest.set({
      page: 0,
      size: 10,
      sortBy: 'createdAt',
      sortDirection: 'DESC',
    });
    this.statusFilter.set(null);
    this.tagIdFilter.set(null);
    this.fetchMaterials();
  }

  get selectedTag(): Tag | undefined {
    const id = this.tagIdFilter();
    return this.allTags().find((t) => t.id === id);
  }

  get tableData(): PaginationResponse<MaterialResponse> | null {
    switch (this.currentTab()) {
      case 'learning':
        return this.learningsData();
      case 'blog':
        return this.blogsData();
      case 'wiki':
        return this.wikisData();
      default:
        return null;
    }
  }

  get totalElements(): number {
    return this.tableData?.totalElements ?? 0;
  }

  get addMaterialLink(): string {
    return `/library/add-${this.currentTab()}`;
  }

  getStatusClass(status: SubmissionStatus): string {
    return status.toLowerCase();
  }

  getTagNames(tags: any[]): string[] {
    return tags.map((t) => t.tagName);
  }

  getHiddenTagsCount(tags: any[]): number {
    return Math.max(0, tags.length - 1);
  }

  onRowClick(material: MaterialResponse): void {
    const materialType = this.currentTab();
    const materialId = material.id;
    this.router.navigate([`/library/${materialType}/${materialId}`]);
  }

  onTagButtonClick() {
    if (this.isAdmin) {
      this.router.navigate(['/library/tags/manage']);
    } else {
      this.router.navigate(['/library/my-tag-requests']);
    }
  }

  get sortDirection(): SortDirection {
    return this.paginationRequest().sortDirection.toLowerCase() as
      | 'asc'
      | 'desc';
  }
}
