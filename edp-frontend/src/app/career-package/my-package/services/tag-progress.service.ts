import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TagProgressResponseDto } from '../models/tag-progress-reponse.dto';

@Injectable({
  providedIn: 'root',
})
export class TagProgressService {
  private baseUrl = 'http://localhost:8081/api/tag-progress';
  private httpClient = inject(HttpClient);

  updateTagProgress(
    tagProgressId: number,
    completedValue: number | null,
    proofUrl: string,
    file: File | null
  ): Observable<TagProgressResponseDto> {
    const formData = new FormData();

    formData.append('completedValue', completedValue !== null ? completedValue.toString() : '0');
    
    formData.append('proofUrl', proofUrl);
    
    if (file) {
      formData.append('file', file, file.name);
    }
    
    return this.httpClient.put<TagProgressResponseDto>(`${this.baseUrl}/${tagProgressId}`, formData);
  }
}