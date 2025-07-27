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
import { catchError, switchMap, throwError } from 'rxjs';

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

        const refreshDto: RefreshRequestDto = { refreshToken };

        return authService.refresh(refreshDto).pipe(
          switchMap((res) => {
            tokenService.saveTokens(res.accessToken, res.refreshToken);
            const retryReq = req.clone({
              headers: req.headers.set('Authorization', `Bearer ${res.accessToken}`)
            });
            return next(retryReq);
          }),
          catchError(() => {
            tokenService.clearTokens();
            return throwError(() => new Error('Session expired. Please login again.'));
          })
        );
      }
      return throwError(() => error);
    })
  );
};
