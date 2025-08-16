import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../../auth/service/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const notificationService = inject(NotificationService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  } else {
    notificationService.showWarning(
      'Access Denied: Only administrators can access this feature.'
    );
    router.navigate(['/dashboard']);
    return false;
  }
};
