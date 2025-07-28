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
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { NotificationService } from '../services/notification.service';
import { UserService } from '../services/user.service';
import { passwordMatchValidator } from '../validators/password-match.validator';
import { passwordStrengthValidator } from '../validators/password-strength.validator';
import { pastDateValidator } from '../validators/past-date.validator';
import { UserRegisterRequestDto } from '../../auth/model/user-register-request.dto';

@Component({
  selector: 'app-user-create',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatCardActions,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
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
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.css'],
})
export class UserCreateComponent implements OnInit, OnDestroy {
  userCreateForm!: FormGroup;
  isAdmin: boolean = false;
  isPasswordVisible: boolean = false;
  isConfirmPasswordVisible: boolean = false;
  private destroy$: Subject<void> = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    public router: Router,
    private userService: UserService,
    private notificationService: NotificationService
  ) {
    this.isAdmin = userService.currentUser()?.admin!;
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.userCreateForm = this.fb.group(
      {
        firstName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(50),
          ],
        ],
        lastName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(50),
          ],
        ],
        username: [
          '',
          [
            Validators.required,
            Validators.minLength(4),
            Validators.maxLength(30),
          ],
        ],
        email: [
          '',
          [Validators.required, Validators.email, Validators.maxLength(100)],
        ],
        password: ['', [Validators.required, passwordStrengthValidator()]],
        confirmPassword: ['', [Validators.required]],
        birthdate: [null, [Validators.required, pastDateValidator()]],
        phoneNumber: ['', [Validators.pattern(/^\+?[0-9. ()-]{7,25}$/)]],
        department: ['', [Validators.maxLength(50)]],
        position: ['', [Validators.maxLength(50)]],
        admin: [{ value: false, disabled: !this.isAdmin }],
        reportsToId: [
          { value: null, disabled: !this.isAdmin },
          [Validators.min(0)],
        ],
      },
      {
        validators: passwordMatchValidator('password', 'confirmPassword'),
      }
    );
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  getFormControl(name: string): AbstractControl | null {
    return this.userCreateForm.get(name);
  }

  getErrorMessage(controlName: string, errorName: string): string {
    const control = this.getFormControl(controlName);
    if (control?.invalid && (control?.touched || control?.dirty)) {
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
    this.userCreateForm.markAllAsTouched();
    if (this.userCreateForm.invalid) {
      this.notificationService.showWarning('Please correct the form errors.');
      return;
    }

    const formValue = this.userCreateForm.getRawValue();
    const createRequest: UserRegisterRequestDto = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      username: formValue.username,
      email: formValue.email,
      password: formValue.password,
      birthdate: formatDate(formValue.birthdate, 'yyyy-MM-dd', 'en-US'),
      phoneNumber: formValue.phoneNumber || undefined,
      department: formValue.department || undefined,
      position: formValue.position || undefined,
      admin: this.isAdmin ? formValue.admin : false,
      reportsToId: this.isAdmin ? formValue.reportsToId : undefined,
    };

    this.userService
      .createUser(createRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess('User created successfully!');
          // this.router.navigate(['/users']);
        },
        error: (err: HttpErrorResponse) => {
          if (err.error && err.error.fieldErrors) {
            // Apply backend validation errors to form controls
            Object.keys(err.error.fieldErrors).forEach((field) => {
              const control = this.userCreateForm.get(field);
              if (control) {
                control.setErrors({
                  serverError: err.error.fieldErrors[field],
                });
                control.markAsTouched();
              }
            });
          }
          console.error('Error creating user:', err);
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
