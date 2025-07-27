import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { AuthRequestDto } from '../model/auth-request.dto';
import { AuthResponseDto } from '../model/auth-response.dto';
import { UserRegisterRequestDto } from '../model/user-register-request.dto';
import { LogoutRequestDto } from '../model/logout-request.dto';
import { RefreshRequestDto } from '../model/refresh-request.dto';
import { ErrorResponse } from '../model/error-response.dto';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';

  private httpClient = inject(HttpClient);
  private tokenService = inject(TokenService);

  login(dto: AuthRequestDto): Observable<AuthResponseDto> {
    return this.httpClient.post<AuthResponseDto>(`${this.API_URL}/login`, dto).pipe(
      tap((res) => this.tokenService.saveTokens(res.accessToken, res.refreshToken)),
      catchError((error) => this.handleError(error, 'login'))
    );
  }

  register(dto: UserRegisterRequestDto): Observable<AuthResponseDto> {
    return this.httpClient.post<AuthResponseDto>(`${this.API_URL}/register`, dto).pipe(
      tap((res) => this.tokenService.saveTokens(res.accessToken, res.refreshToken)),
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
    return this.httpClient.post<AuthResponseDto>(`${this.API_URL}/refresh`, dto).pipe(
      tap((res) => this.tokenService.saveTokens(res.accessToken, res.refreshToken)),
      catchError((error) => this.handleError(error, 'refresh'))
    );
  }

  private handleError(error: HttpErrorResponse, operation: string): Observable<never> {
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
