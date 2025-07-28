import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { UserService } from '../services/user.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  if (userService.currentUser()?.admin) {
    return true;
  } else {
    notificationService.showWarning(
      'Access Denied: Only administrators can create users.'
    );
    // router.navigate(['/']);
    return false;
  }
};
