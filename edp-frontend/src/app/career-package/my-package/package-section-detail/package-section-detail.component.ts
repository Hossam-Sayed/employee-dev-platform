import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { of } from 'rxjs';
import { switchMap, tap, catchError } from 'rxjs/operators';
import { PackageDetailService } from '../services/package-store.service';
import { SectionProgressService } from '../services/section-progress.service';
import { FileService } from '../services/file.service';
import { SectionProgressResponseDto } from '../models/section-progress-response.dto';
import { MatDialog } from '@angular/material/dialog';
import { TagProgressService } from '../services/tag-progress.service';
import { UpdateTagProgressDialogComponent } from './update-tag-progress-dialog/update-tag-progress-dialog.component';
import { TagProgressResponseDto } from '../models/tag-progress-reponse.dto';
import { MatIcon } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-section-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatProgressBarModule,
    MatButtonModule,
    RouterLink,
    MatIcon,
    MatTooltipModule
  ],
  templateUrl: './package-section-detail.component.html',
  styleUrls: ['./package-section-detail.component.css'],
})
export class PackageSectionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private packageDetailService = inject(PackageDetailService);
  private sectionProgressService = inject(SectionProgressService);
  private fileService = inject(FileService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private tagProgressService = inject(TagProgressService);

  loading = signal(true);
  sectionData = signal<SectionProgressResponseDto | null>(null);

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const sectionProgressId = Number(params.get('sectionProgressId'));
          if (isNaN(sectionProgressId)) {
            this.snackBar.open('Invalid section ID.', 'Close', {
              duration: 3000,
            });
            this.loading.set(false);
            return of(null);
          }

          this.loading.set(true);

          const careerPackage = this.packageDetailService.package();
          let sectionFromSignal: SectionProgressResponseDto | undefined;

          if (careerPackage && careerPackage.sections) {
            sectionFromSignal = careerPackage.sections.find(
              (s) => s.sectionProgressId === sectionProgressId
            );
          }

          if (sectionFromSignal) {
            this.sectionData.set(sectionFromSignal);
            return this.sectionProgressService
              .listTagsProgress(sectionProgressId)
              .pipe(
                tap((tags) =>
                  this.sectionData.set({ ...this.sectionData()!, tags: tags })
                ),
                catchError((err) => {
                  this.snackBar.open('Failed to load tags progress.', 'Close', {
                    duration: 3000,
                  });
                  return of(null);
                })
              );
          } else {
            return this.sectionProgressService
              .getSectionProgress(sectionProgressId)
              .pipe(
                tap((sectionResponse) => this.sectionData.set(sectionResponse)),
                switchMap((sectionResponse) => {
                  if (sectionResponse) {
                    return this.sectionProgressService
                      .listTagsProgress(sectionProgressId)
                      .pipe(
                        tap((tags) =>
                          this.sectionData.set({
                            ...sectionResponse,
                            tags: tags,
                          })
                        ),
                        catchError((err) => {
                          this.snackBar.open(
                            'Failed to load tags for direct access.',
                            'Close',
                            { duration: 3000 }
                          );
                          return of(null);
                        })
                      );
                  }
                  return of(null);
                }),
                catchError((err) => {
                  this.snackBar.open(
                    'Failed to load section details.',
                    'Close',
                    { duration: 3000 }
                  );
                  return of(null);
                })
              );
          }
        })
      )
      .subscribe({
        next: () => this.loading.set(false),
        error: () => this.loading.set(false),
      });
  }

  isUrl(url: string): boolean {
    if (!url) {
      return false;
    }
    try {
      new URL(url);
      return true;
    } catch (e) {
      return false;
    }
  }

  viewPdfProof(fileId: string): void {
    this.fileService.getFile(fileId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => {
        this.snackBar.open('Failed to load PDF proof.', 'Close', {
          duration: 3000,
        });
        console.error('Error fetching file:', err);
      },
    });
  }

  onUpdateProgressClick(tag: TagProgressResponseDto): void {
    const dialogRef = this.dialog.open(UpdateTagProgressDialogComponent, {
      width: '500px',
      data: tag,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loading.set(true);
        const sectionProgressId = this.sectionData()!.sectionProgressId;

        this.tagProgressService
          .updateTagProgress(
            tag.tagProgressId,
            result.completedValue,
            result.proofUrl,
            result.file
          )
          .subscribe({
            next: (updatedTag) => {
              const currentSectionData = this.sectionData();
              if (currentSectionData && currentSectionData.tags) {
                const updatedTags = currentSectionData.tags.map((t) =>
                  t.tagProgressId === updatedTag.tagProgressId ? updatedTag : t
                );
                this.sectionData.set({
                  ...currentSectionData,
                  tags: updatedTags,
                });
              }
              this.loading.set(false);
              this.snackBar.open(
                'Tag progress updated successfully!',
                'Close',
                { duration: 3000 }
              );
            },
            error: (err) => {
              this.loading.set(false);
              this.snackBar.open(
                'Failed to update tag progress:' + err.error.message,
                'Close',
                {
                  duration: 3000,
                }
              );
              console.error('Update error:', err);
            },
          });
      }
    });
  }
}
