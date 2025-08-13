import { Component, input, output } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NotificationSubmission } from '../models/notification-submission.model';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, DatePipe],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.css',
})
export class NotificationListComponent {
  readonly notifications = input<NotificationSubmission[]>([]);
  readonly notificationClick = output<NotificationSubmission>();

  onClick(notification: NotificationSubmission) {
    this.notificationClick.emit(notification);
  }
}
