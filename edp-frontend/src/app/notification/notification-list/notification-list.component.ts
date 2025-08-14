import { Component, inject, input, output } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NotificationSubmission } from '../models/notification-submission.model';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    DatePipe,
  ],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.css',
})
export class NotificationListComponent {
  readonly notifications = input<NotificationSubmission[]>([]);
  readonly notificationClick = output<NotificationSubmission>();

  private router = inject(Router);

  onClick(notification: NotificationSubmission) {
    this.notificationClick.emit(notification);
    let route = '';
    switch (notification.submissionType) {
      case 'PACKAGE':
        if (notification.status === 'PENDING') {
          route = `/career-package/submissions/action/${notification.submissionId}/`;
        } else {
          route = `/career-package/submissions/my-package/${notification.submissionId}/`;
        }
        break;
      case 'TAG':
        route = 'library/my-tag-requests';
        break;
      default:
        route = `/library/${notification.submissionType.toLocaleLowerCase()}/${
          notification.submissionId
        }/`;
        break;
    }
    this.router.navigate([route]);
  }
}
