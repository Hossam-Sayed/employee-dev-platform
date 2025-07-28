import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../services/user.service';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { NotificationService } from '../services/notification.service';
import { UserResponse } from '../models/user-response.model';
import { UserUpdateRequest } from '../models/user-update-request.model';
import { passwordMatchValidator } from '../validators/password-match.validator';
import { pastDateValidator } from '../validators/past-date.validator';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-update',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatCardActions,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatSnackBarModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatSlideToggleModule,
    MatSelectModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.css'],
})
export class UserUpdateComponent implements OnInit, OnDestroy {
  userUpdateForm!: FormGroup;
  userId!: number;
  isAdmin: boolean = false;
  isPasswordVisible: boolean = false;
  isConfirmPasswordVisible: boolean = false;
  private destroy$: Subject<void> = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.userId = this.route.snapshot.params['id'];
    this.initForm();
    this.loadUserData(this.userId);
    this.setupPasswordConditionalValidation();
  }

  initForm(): void {
    this.userUpdateForm = this.fb.group(
      {
        firstName: ['', [Validators.minLength(2), Validators.maxLength(50)]],
        lastName: ['', [Validators.minLength(2), Validators.maxLength(50)]],
        username: ['', [Validators.minLength(4), Validators.maxLength(30)]],
        email: ['', [Validators.email, Validators.maxLength(100)]],
        birthdate: [null, [Validators.required, pastDateValidator()]],
        phoneNumber: ['', [Validators.pattern(/^\+?[0-9. ()-]{7,25}$/)]],
        department: ['', [Validators.maxLength(50)]],
        position: ['', [Validators.maxLength(50)]],
        admin: [false],
        reportsToId: [null, [Validators.min(0)]],
        changePassword: [false],
        password: [''],
        confirmPassword: [''],
      },
      {
        validators: passwordMatchValidator('password', 'confirmPassword'),
      }
    );
  }

  loadUserData(id: number): void {
    this.userService
      .getUser(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user: UserResponse) => {
          this.isAdmin = user.admin;
          this.userUpdateForm.patchValue({
            firstName: user.firstName,
            lastName: user.lastName,
            username: user.username,
            email: user.email,
            birthdate: user.birthdate ? new Date(user.birthdate) : null,
            phoneNumber: user.phoneNumber,
            department: user.department,
            position: user.position,
            admin: user.admin,
            reportsToId: user.reportsToId,
          });
        },
        error: (err: HttpErrorResponse) => {
          console.error('Failed to load user data:', err);
        },
      });
  }

  setupPasswordConditionalValidation(): void {
    const passwordControl = this.userUpdateForm.get('password');
    const confirmPasswordControl = this.userUpdateForm.get('confirmPassword');
    const changePasswordControl = this.userUpdateForm.get('changePassword');

    if (changePasswordControl && passwordControl && confirmPasswordControl) {
      changePasswordControl.valueChanges
        .pipe(takeUntil(this.destroy$))
        .subscribe((checked) => {
          if (checked) {
            passwordControl.setValidators([Validators.required]);
            confirmPasswordControl.setValidators([Validators.required]);
          } else {
            passwordControl.clearValidators();
            confirmPasswordControl.clearValidators();
            passwordControl.setValue('');
            confirmPasswordControl.setValue('');
          }
          passwordControl.updateValueAndValidity();
          confirmPasswordControl.updateValueAndValidity();
          this.userUpdateForm.updateValueAndValidity();
        });
    }
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  getFormControl(name: string): AbstractControl | null {
    return this.userUpdateForm.get(name);
  }

  getErrorMessage(controlName: string, errorName: string): string {
    const control = this.getFormControl(controlName);
    if (control?.touched || control?.dirty) {
      if (control?.hasError(errorName)) {
        switch (errorName) {
          case 'required':
            return 'This field is required.';
          case 'minlength':
            return `Must be at least ${control.errors?.['minlength'].requiredLength} characters.`;
          case 'maxlength':
            return `Cannot exceed ${control.errors?.['maxlength'].requiredLength} characters.`;
          case 'email':
            return 'Invalid email format.';
          case 'pattern':
            if (controlName === 'phoneNumber')
              return 'Invalid phone number format.';
            return 'Invalid format.';
          case 'passwordStrength':
            const errors = control.errors?.['passwordStrength'];
            let msg = 'Password must contain: ';
            if (!errors.hasMinLength) msg += '8+ chars, ';
            if (!errors.hasUppercase) msg += 'uppercase, ';
            if (!errors.hasNumber) msg += 'number, ';
            if (!errors.hasSpecialCharacter) msg += 'special char, ';
            return msg.slice(0, -2) + '.';
          case 'pastDate':
            return 'Birthdate must be in the past.';
          case 'passwordMismatch':
            return 'Passwords do not match.';
          case 'serverError':
            return control.errors?.['serverError'];
        }
      }
    }
    return '';
  }

  onSubmit(): void {
    this.userUpdateForm.markAllAsTouched();
    if (this.userUpdateForm.invalid) {
      this.notificationService.showWarning('Please correct the form errors.');
      return;
    }

    const formValue = this.userUpdateForm.value;
    const updateRequest: UserUpdateRequest = {
      firstName: formValue.firstName || undefined,
      lastName: formValue.lastName || undefined,
      username: formValue.username || undefined,
      email: formValue.email || undefined,
      birthdate: formValue.birthdate
        ? formatDate(formValue.birthdate, 'yyyy-MM-dd', 'en-US')
        : undefined,
      phoneNumber: formValue.phoneNumber || undefined,
      department: formValue.department || undefined,
      position: formValue.position || undefined,
      admin: this.isAdmin ? formValue.admin : undefined,
      reportsToId: this.isAdmin ? formValue.reportsToId : undefined,
      password: formValue.changePassword ? formValue.password : undefined,
    };

    this.userService
      .updateUser(this.userService.currentUser()?.id!, updateRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess('User updated successfully!');
          // TODO: navigate or add proper action
          // this.router.navigate(['/users', this.userId]);
        },
        error: (err: HttpErrorResponse) => {
          if (err.error && err.error.fieldErrors) {
            Object.keys(err.error.fieldErrors).forEach((field) => {
              const control = this.userUpdateForm.get(field);
              if (control) {
                control.setErrors({
                  serverError: err.error.fieldErrors[field],
                });
                control.markAsTouched();
              }
            });
          }
          console.error('Error updating user:', err);
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
