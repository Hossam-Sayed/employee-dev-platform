import { Component, inject, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SectionProgressResponseDto } from '../../models/section-progress-response.dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-package-section',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './package-section.component.html',
  styleUrls: ['./package-section.component.css'],
})
export class PackageSectionComponent {
  sectionData = input.required<SectionProgressResponseDto>();
  sectionIndex = input.required<number>();
  private router = inject(Router);

  onDetailsClick(): void {
    this.router.navigate([
      'career-package',
      'my-package',
      'sections',
      this.sectionData().sectionProgressId,
    ]);
  }
}
