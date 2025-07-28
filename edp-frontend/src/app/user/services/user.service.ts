import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../models/user-response.model';
import { UserUpdateRequest } from '../models/user-update-request.model';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  private _currentUser = signal<User | null>(null);
  readonly currentUser = computed(() => this._currentUser());

  private http = inject(HttpClient);

  setAuthenticatedUser(user: User | null): void {
    this._currentUser.set(user);
  }

  updateCurrentUserSignal(updater: (prev: User | null) => User) {
    this._currentUser.update(updater);
  }

  clearCurrentUser(): void {
    this._currentUser.set(null);
  }

  getUser(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/${id}`);
  }

  updateUser(id: number, userData: UserUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, userData);
  }
}
