import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../../auth/service/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const notificationService = inject(NotificationService);

  if (authService.isAdmin()) {
    return true;
  } else {
    notificationService.showWarning(
      'Access Denied: Only administrators can create users.'
    );
    // router.navigate(['/']);
    return false;
  }
};
