import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-custom-tag',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './custom-tag.component.html',
  styleUrls: ['./custom-tag.component.css'],
})
export class CustomTagComponent {
  tagName = input<string>('');
  status = input<string | null>(null);
  closable = input<boolean>(false);
  durationMinutes = input<number | null>(null);

  remove = output<void>();

  statusClass = computed(() => {
    const status = this.status()?.toLowerCase();
    return status === 'approved' ||
      status === 'pending' ||
      status === 'rejected'
      ? `status-${status}`
      : 'status-default';
  });

  iconName = computed(() => {
    const status = this.status()?.toLowerCase();
    switch (status) {
      case 'approved':
        return 'check_circle';
      case 'pending':
        return 'timelapse';
      case 'rejected':
        return 'cancel';
      default:
        return null;
    }
  });

  onRemoveClick(event: MouseEvent) {
    event.stopPropagation();
    this.remove.emit();
  }
}
