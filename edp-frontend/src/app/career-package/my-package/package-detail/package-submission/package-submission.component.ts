import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { SubmissionResponseDto } from '../../models/submission-response.dto';

@Component({
  selector: 'app-package-submission',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
  ],
  templateUrl: './package-submission.component.html',
  styleUrls: ['./package-submission.component.css']
})
export class PackageSubmissionComponent {
  submissionData = input.required<SubmissionResponseDto>();
}