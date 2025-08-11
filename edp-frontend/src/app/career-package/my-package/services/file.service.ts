import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private readonly baseUrl = 'http://localhost:8085/api/files';
  private http = inject(HttpClient);

  getFile(fileId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${fileId}`, { responseType: 'blob' });
  }
}
