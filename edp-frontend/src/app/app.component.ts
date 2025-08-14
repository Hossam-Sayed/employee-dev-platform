import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { AuthService } from './auth/service/auth.service';
import { NotificationStreamService } from './notification/services/notification-stream.service';
import { NotificationStateService } from './notification/services/notification-state.service';
import { tap, switchMap } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'edp-frontend';

  private authService = inject(AuthService);
  private notificationStreamService = inject(NotificationStreamService);
  private notificationStateService = inject(NotificationStateService);

  ngOnInit(): void {
    // TODO: search initialize before update and double request
    // this.authService.setUserFromToken().subscribe();

    const token = localStorage.getItem('access_token');
    if (!token) return;

    this.notificationStreamService
      .getMyNotifications()
      .pipe(
        tap((page) => {
          // Add the fetched notifications to state
          this.notificationStateService.setNotifications(page.content);
        }),
        switchMap(() => {
          // Now start SSE connection only after HTTP success
          return this.notificationStreamService.connect();
        })
      )
      .subscribe({
        next: (notification) => {
          console.log('New SSE notification:', notification);
          this.notificationStateService.addNotification(notification);
        },
        error: (err) => console.error('Error in notification flow', err),
      });
  }
}
