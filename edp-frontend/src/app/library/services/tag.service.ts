import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tag } from '../models/tag.model';

@Injectable({
  providedIn: 'root',
})
export class TagService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8082/api/tags/';

  getAllActiveTags(nameFilter?: string): Observable<Tag[]> {
    let params = new HttpParams();
    if (nameFilter) {
      params = params.set('nameFilter', nameFilter);
    }
    return this.http.get<Tag[]>(`${this.baseUrl}active`, { params });
  }
}
