import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { ErrorResponse } from '../models/error-response.model';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const notificationService = inject(NotificationService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred!';
      let fieldErrors: { [key: string]: string } = {};

      if (error.error instanceof ErrorEvent) {
        // Client-side or network error
        errorMessage = `Network Error: ${error.error.message}`;
        notificationService.showError(errorMessage);
      } else {
        // Backend error
        const backendError = error.error as ErrorResponse;
        switch (error.status) {
          case 400: // Bad Request
            if (backendError && backendError.message) {
              errorMessage = backendError.message;

              // Attempt to parse field-specific errors if the format matches
              const fieldErrorRegex = /(\w+): (.+?);/g;
              let match;
              let hasFieldSpecificErrors = false;
              while ((match = fieldErrorRegex.exec(errorMessage)) !== null) {
                fieldErrors[match[1]] = match[2];
                hasFieldSpecificErrors = true;
              }

              if (!hasFieldSpecificErrors) {
                notificationService.showError(errorMessage);
              }
              // Field-specific errors will be handled by the form component
            } else {
              notificationService.showError(
                'Bad Request: Invalid data provided.'
              );
            }
            break;
          case 401: // Unauthorized
            errorMessage =
              backendError?.message || 'Unauthorized: Please log in.';
            notificationService.showError(errorMessage);
            // Redirect to login page
            break;
          case 403: // Forbidden
            errorMessage =
              backendError?.message ||
              'Forbidden: You do not have permission to access this resource.';
            notificationService.showWarning(errorMessage);
            // Redirect to an access denied page
            break;
          case 404: // Not Found
            errorMessage = backendError?.message || 'Resource not found.';
            notificationService.showWarning(errorMessage);
            break;
          case 409: // Conflict (e.g., DataIntegrityViolationException)
            errorMessage =
              backendError?.message ||
              'Data conflict: The resource could not be processed due to a conflict.';
            notificationService.showWarning(errorMessage);
            break;
          case 500: // Internal Server Error
            errorMessage =
              backendError?.message ||
              'Internal Server Error: An unexpected error occurred on the server.';
            notificationService.showError(errorMessage);
            break;
          default:
            errorMessage =
              backendError?.message ||
              `Error ${error.status}: ${error.statusText}`;
            notificationService.showError(errorMessage);
            break;
        }
      }

      // Re-throw the error to be handled by the component if needed,
      // potentially with parsed field errors.
      const customError = new HttpErrorResponse({
        error: { ...error.error, fieldErrors: fieldErrors },
        headers: error.headers,
        status: error.status,
        statusText: error.statusText,
        url: error.url!,
      });
      return throwError(() => customError);
    })
  );
};
