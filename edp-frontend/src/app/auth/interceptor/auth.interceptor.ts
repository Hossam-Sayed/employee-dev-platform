import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse
} from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../service/token.service';
import { AuthService } from '../service/auth.service';
import { RefreshRequestDto } from '../model/refresh-request.dto';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);

  const isAuthEndpoint =
    req.url.includes('/login') ||
    req.url.includes('/register') ||
    req.url.includes('/refresh');

  if (isAuthEndpoint) {
    return next(req);
  }

  const accessToken = tokenService.getAccessToken();
  const authReq = accessToken
    ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${accessToken}`) })
    : req;

  return next(authReq).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        const refreshToken = tokenService.getRefreshToken();

        if (!refreshToken) {
          tokenService.clearTokens();
          return throwError(() => new Error('Unauthorized and no refresh token available'));
        }

        if (isRefreshing) {
          return refreshTokenSubject.pipe(
            filter(token => token !== null),
            take(1),
            switchMap((token) => {
              const retryReq = req.clone({
                headers: req.headers.set('Authorization', `Bearer ${token}`)
              });
              return next(retryReq);
            })
          );
        }

        isRefreshing = true;
        refreshTokenSubject.next(null);

        const refreshDto: RefreshRequestDto = { refreshToken };

        return authService.refresh(refreshDto).pipe(
          switchMap((res) => {
            tokenService.saveTokens(res.accessToken, res.refreshToken);
            isRefreshing = false;
            refreshTokenSubject.next(res.accessToken); 

            const retryReq = req.clone({
              headers: req.headers.set('Authorization', `Bearer ${res.accessToken}`)
            });
            return next(retryReq);
          }),
          catchError((err) => {
            isRefreshing = false;
            tokenService.clearTokens();
            refreshTokenSubject.next(null);
            return throwError(() => new Error('Session expired. Please login again.'));
          })
        );
      }

      return throwError(() => error);
    })
  );
};
