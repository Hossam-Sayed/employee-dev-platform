import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  showSuccess(
    message: string,
    action: string = 'Close',
    duration: number = 3000
  ): void {
    this.snackBar.open(message, action, {
      duration: duration,
      panelClass: ['success-snackbar'],
    });
  }

  showError(
    message: string,
    action: string = 'Close',
    duration: number = 5000
  ): void {
    this.snackBar.open(message, action, {
      duration: duration,
      panelClass: ['error-snackbar'],
    });
  }

  showWarning(
    message: string,
    action: string = 'Close',
    duration: number = 4000
  ): void {
    this.snackBar.open(message, action, {
      duration: duration,
      panelClass: ['warning-snackbar'],
    });
  }
}
