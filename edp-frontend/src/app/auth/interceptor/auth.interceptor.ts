import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../service/token.service';
import { throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { TokenRefreshService } from '../service/token-refresh.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
) => {
  const tokenService = inject(TokenService);
  const tokenRefreshService = inject(TokenRefreshService);

  const isAuthEndpoint =
    req.url.includes('/login') ||
    req.url.includes('/register') ||
    req.url.includes('/refresh');

  if (isAuthEndpoint) {
    return next(req);
  }

  const accessToken = tokenService.getAccessToken();
  const authReq = accessToken
    ? req.clone({
        headers: req.headers.set('Authorization', `Bearer ${accessToken}`),
      })
    : req;

  return next(authReq).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        const retryFn = (newToken: string) => {
          const retryReq = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${newToken}`),
          });
          return next(retryReq);
        };
        return tokenRefreshService.refreshAndRetry(retryFn);
      }
      return throwError(() => error);
    })
  );
};
