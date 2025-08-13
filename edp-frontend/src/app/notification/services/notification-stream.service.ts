import { inject, Injectable } from '@angular/core';
import { Observable, shareReplay, Subscriber, from } from 'rxjs';
import { NotificationSubmission } from '../models/notification-submission.model';
import { TokenService } from '../../auth/service/token.service';
import { TokenRefreshService } from '../../auth/service/token-refresh.service';

@Injectable({ providedIn: 'root' })
export class NotificationStreamService {
  private apiUrl = 'http://localhost:8084/api/notifications';
  private stream$?: Observable<NotificationSubmission>;
  private tokenService = inject(TokenService);
  private tokenRefreshService = inject(TokenRefreshService);

  connect(): Observable<NotificationSubmission> {
    if (!this.stream$) {
      this.stream$ = new Observable<NotificationSubmission>((observer) => {
        let es: EventSource;

        const openConnection = (token: string) => {
          es = new EventSource(`${this.apiUrl}/stream?token=${token}`);

          es.addEventListener('notification', (event: any) => {
            observer.next(JSON.parse(event.data));
          });

          es.onerror = async (err: any) => {
            es.close();

            const retryFn = (newToken: string) => {
              // This is where the retry happens
              return new Promise<void>((resolve) => {
                console.log('NEW TOKEN:', newToken);

                openConnection(newToken);
                resolve();
              });
            };

            // TODO: Debug this logic.
            // this.tokenRefreshService.refreshAndRetry(retryFn).subscribe({
            //   next: () =>
            //     console.log('SSE connection re-established with new token'),
            //   error: (refreshErr) => {
            //     console.error('Failed to refresh token during SSE', refreshErr);
            //     observer.error(refreshErr);
            //   },
            // });
          };
        };

        const token = this.tokenService.getAccessToken();
        if (token) {
          openConnection(token);
        } else {
          observer.error('No access token found');
        }

        return () => {
          if (es) {
            es.close();
          }
        };
      }).pipe(shareReplay(1));
    }
    return this.stream$;
  }
}
