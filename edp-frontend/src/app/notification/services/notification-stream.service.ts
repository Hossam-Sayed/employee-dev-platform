import { inject, Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { NotificationSubmission } from '../models/notification-submission.model';
import { TokenService } from '../../auth/service/token.service';
import { HttpClient } from '@angular/common/http';
import { Page } from '../../shared/models/page.dto';

@Injectable({ providedIn: 'root' })
export class NotificationStreamService {
  private apiUrl = 'http://localhost:8084/api/notifications';
  private stream$?: Observable<NotificationSubmission>;
  private tokenService = inject(TokenService);
  private http = inject(HttpClient);

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
            console.log('Error connecting to SSE service');
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

  getMyNotifications(read?: boolean, page: number = 0, size: number = 20) {
    const params: any = { page, size };
    if (read !== undefined) params.read = read;
    return this.http.get<Page<NotificationSubmission>>(this.apiUrl, { params });
  }
}
