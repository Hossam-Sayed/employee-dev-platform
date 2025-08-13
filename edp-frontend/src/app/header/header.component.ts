import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenu, MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';

import { TokenService } from '../auth/service/token.service';
import { AuthService } from '../auth/service/auth.service';
import { LogoutRequestDto } from '../auth/model/logout-request.dto';
import { NotificationSubmission } from '../notification/models/notification-submission.model';
import { NotificationListComponent } from '../notification/notification-list/notification-list.component';
import { NotificationStateService } from '../notification/services/notification-state.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatBadgeModule,
    MatMenu,
    NotificationListComponent,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  tokenService = inject(TokenService);
  authService = inject(AuthService);
  router = inject(Router);
  private notificationState = inject(NotificationStateService);

  // Convert streams -> signals for easy template consumption
  readonly notifications = toSignal<NotificationSubmission[]>(
    this.notificationState.notifications$
  );
  readonly unreadCount = toSignal<number>(this.notificationState.unreadCount$);

  goTo(path: string) {
    this.router.navigate([path]);
  }

  onLogout() {
    const username = this.tokenService.getPayload()?.sub;
    if (!username) return;
    const logoutRequest: LogoutRequestDto = { username };
    this.authService.logout(logoutRequest).subscribe({
      next: () => {
        this.router.navigate(['/auth']);
        this.tokenService.clearTokens();
      },
      error: (error) => console.error('Logout failed', error),
    });
  }

  onNotificationClick(notification: NotificationSubmission) {
    // Hook for mark-as-read / navigation later
    console.log('Clicked notification:', notification);
  }
}
