import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SectionProgressResponseDto } from '../../models/section-progress-response.dto';

@Component({
  selector: 'app-package-section',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './package-section.component.html',
  styleUrls: ['./package-section.component.css']
})
export class PackageSectionComponent {
  sectionData = input.required<SectionProgressResponseDto>();
  sectionIndex = input.required<number>();

  onDetailsClick(): void {
    console.log('Details button clicked for section:', this.sectionData().sectionName);
  }
}