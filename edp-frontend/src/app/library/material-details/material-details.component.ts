import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { EMPTY, switchMap, catchError, Observable } from 'rxjs';
import { LibraryService } from '../services/library.service';
import { LearningResponse } from '../models/learning-response.model';
import { BlogResponse } from '../models/blog-response.model';
import { WikiResponse } from '../models/wiki-response.model';
import { AuthService } from '../../auth/service/auth.service';
import { LearningTagResponse } from '../models/learning-tag-response.model';
import { MaterialType } from '../models/material.type';
import { CustomTagComponent } from '../custom-tag/custom-tag.component';
import { MaterialResponse } from '../models/material-response.type';

@Component({
  selector: 'app-material-details',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    RouterLink,
    CustomTagComponent,
  ],
  templateUrl: './material-details.component.html',
  styleUrls: ['./material-details.component.css'],
})
export class MaterialDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private libraryService = inject(LibraryService);
  private authService = inject(AuthService);

  material = signal<MaterialResponse | null>(null);
  materialType = signal<MaterialType | null>(null);
  isOwner = signal(false);
  isRejected = signal(false);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const materialId = Number(params.get('materialId'));
          const type = this.route.snapshot.url[1]?.path as MaterialType;

          if (!materialId || !type) {
            this.error.set('Invalid URL. Missing material type or ID.');
            return EMPTY;
          }

          this.materialType.set(type);
          this.isLoading.set(true);

          let details$: Observable<MaterialResponse>;
          switch (type) {
            case 'learning':
              details$ = this.libraryService.getLearningDetails(materialId);
              break;
            case 'blog':
              details$ = this.libraryService.getBlogDetails(materialId);
              break;
            case 'wiki':
              details$ = this.libraryService.getWikiDetails(materialId);
              break;
            default:
              this.error.set('Unsupported material type.');
              return EMPTY;
          }

          return details$.pipe(
            catchError((err) => {
              this.error.set(
                'Failed to fetch material details. It may not exist.'
              );
              console.error('Error fetching material details:', err);
              this.isLoading.set(false);
              return EMPTY;
            })
          );
        })
      )
      .subscribe((material) => {
        this.material.set(material);
        this.checkUserPermissions(material);
        this.isLoading.set(false);
      });
  }

  private checkUserPermissions(material: MaterialResponse): void {
    const userId = this.authService.getUserId();
    if (userId) {
      const ownerId =
        'employeeId' in material ? material.employeeId : material.authorId;
      this.isOwner.set(userId === ownerId);
      this.isRejected.set(material.status === 'REJECTED');
    }
  }

  get learningMaterial(): LearningResponse | null {
    return this.isLearning(this.material())
      ? (this.material() as LearningResponse)
      : null;
  }

  get blogOrWikiMaterial(): BlogResponse | WikiResponse | null {
    return !this.isLearning(this.material())
      ? (this.material() as BlogResponse | WikiResponse)
      : null;
  }

  get learningTags(): LearningTagResponse[] {
    return this.learningMaterial?.tags ?? [];
  }

  isLearning(material: MaterialResponse | null): material is LearningResponse {
    return !!material && 'proofUrl' in material;
  }
}
