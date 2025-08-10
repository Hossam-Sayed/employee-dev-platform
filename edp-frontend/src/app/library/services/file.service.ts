import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private readonly baseUrl = 'http://localhost:8085/api/files';
  private http = inject(HttpClient);

  /**
   * Fetches a file from the backend as a Blob.
   * This is necessary for handling binary data like documents.
   * @param fileId The ID of the file to fetch.
   * @returns An Observable of the file as a Blob.
   */
  getFile(fileId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${fileId}`, { responseType: 'blob' });
  }
}
