import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';
import { NotificationSubmission } from '../models/notification-submission.model';

@Injectable({ providedIn: 'root' })
export class NotificationStateService {
  private notificationsSubject = new BehaviorSubject<NotificationSubmission[]>(
    []
  );
  notifications$ = this.notificationsSubject.asObservable();

  addNotification(notification: NotificationSubmission) {
    const current = this.notificationsSubject.getValue();
    this.notificationsSubject.next([notification, ...current]);
  }

  setNotifications(notifications: NotificationSubmission[]) {
    this.notificationsSubject.next(notifications);
  }

  unreadCount$ = this.notifications$.pipe(
    map((list) => list.filter((n) => !n.read).length)
  );
}
