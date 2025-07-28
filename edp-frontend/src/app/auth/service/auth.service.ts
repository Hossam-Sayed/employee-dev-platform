import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { AuthRequestDto } from '../model/auth-request.dto';
import { AuthResponseDto } from '../model/auth-response.dto';
import { UserRegisterRequestDto } from '../model/user-register-request.dto';
import { LogoutRequestDto } from '../model/logout-request.dto';
import { RefreshRequestDto } from '../model/refresh-request.dto';
import { ErrorResponse } from '../model/error-response.dto';
import { TokenService } from './token.service';
import { User } from '../../user/models/user.model';
import { UserService } from '../../user/services/user.service';
import { UserResponse } from '../../user/models/user-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';

  private httpClient = inject(HttpClient);
  private tokenService = inject(TokenService);
  private userService = inject(UserService);

  private fetchAndSetUser(userId: number): Observable<void> {
    return this.userService.getUser(userId).pipe(
      tap((userResponse: UserResponse) => {
        const fullUser: User = {
          ...userResponse,
        };

        this.userService.setAuthenticatedUser(fullUser);
      }),
      map(() => {}),
      catchError((err) => {
        console.error('Failed to fetch full user details:', err);
        this.userService.clearCurrentUser();
        this.tokenService.clearTokens();
        return throwError(() => new Error('Failed to load user profile.'));
      })
    );
  }

  setUserFromToken(): Observable<void> {
    const payload = this.tokenService.getPayload();

    if (!payload || !payload.userId) {
      this.userService.clearCurrentUser();
      return of(undefined);
    }

    return this.fetchAndSetUser(payload.userId);
  }

  login(dto: AuthRequestDto): Observable<AuthResponseDto> {
    return this.httpClient
      .post<AuthResponseDto>(`${this.API_URL}/login`, dto)
      .pipe(
        tap((res) =>
          this.tokenService.saveTokens(res.accessToken, res.refreshToken)
        ),
        catchError((error) => this.handleError(error, 'login'))
      );
  }

  register(dto: UserRegisterRequestDto): Observable<AuthResponseDto> {
    return this.httpClient
      .post<AuthResponseDto>(`${this.API_URL}/register`, dto)
      .pipe(
        tap((res) =>
          this.tokenService.saveTokens(res.accessToken, res.refreshToken)
        ),
        catchError((error) => this.handleError(error, 'register'))
      );
  }

  logout(dto: LogoutRequestDto): Observable<void> {
    return this.httpClient.post<void>(`${this.API_URL}/logout`, dto).pipe(
      tap(() => this.tokenService.clearTokens()),
      catchError((error) => this.handleError(error, 'logout'))
    );
  }

  refresh(dto: RefreshRequestDto): Observable<AuthResponseDto> {
    return this.httpClient
      .post<AuthResponseDto>(`${this.API_URL}/refresh`, dto)
      .pipe(
        tap((res) =>
          this.tokenService.saveTokens(res.accessToken, res.refreshToken)
        ),
        catchError((error) => this.handleError(error, 'refresh'))
      );
  }

  private handleError(
    error: HttpErrorResponse,
    operation: string
  ): Observable<never> {
    console.error(`AuthService ${operation} failed`, error);

    let message = 'An unexpected error occurred';

    if (error.error && typeof error.error === 'object') {
      const err: ErrorResponse = error.error;
      message = err.message || message;
    } else if (error.status === 0) {
      message = 'Cannot connect to the server. Please try again later.';
    }

    return throwError(() => new Error(message));
  }
}
