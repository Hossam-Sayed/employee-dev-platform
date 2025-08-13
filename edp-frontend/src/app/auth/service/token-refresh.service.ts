import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError, from } from 'rxjs';
import { filter, take, switchMap, catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';
import { RefreshRequestDto } from '../model/refresh-request.dto';

@Injectable({
  providedIn: 'root',
})
export class TokenRefreshService {
  private authService = inject(AuthService);
  private tokenService = inject(TokenService);

  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  refreshAndRetry<T>(
    retryFn: (accessToken: string) => Observable<T> | Promise<T>
  ): Observable<T> {
    if (this.isRefreshing) {
      console.log('REFRESHING');

      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap((token) => {
          console.log('RETRY TOKEN', token);

          return from(retryFn(token!));
        })
      );
    }

    console.log('START REFRESHNG');

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      this.tokenService.clearTokens();
      this.isRefreshing = false;
      return throwError(() => new Error('No refresh token available.'));
    }

    console.log('TOKEN USED:', refreshToken);

    const refreshDto: RefreshRequestDto = { refreshToken };

    return this.authService.refresh(refreshDto).pipe(
      switchMap((res) => {
        const { accessToken, refreshToken: newRefreshToken } = res;
        this.tokenService.saveTokens(accessToken, newRefreshToken);
        console.log('REFRESH', newRefreshToken);
        console.log('ACCESS', accessToken);

        this.isRefreshing = false;
        this.refreshTokenSubject.next(accessToken);

        return from(retryFn(accessToken));
      }),
      catchError((err) => {
        console.log('FAILED');
        this.isRefreshing = false;
        this.tokenService.clearTokens();
        this.refreshTokenSubject.next(null);

        return throwError(
          () => new Error('Session expired. Please log in again.')
        );
      })
    );
  }
}
