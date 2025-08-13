import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

import { Component, inject, OnInit, signal } from '@angular/core';
import {
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { connect, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatStepperModule } from '@angular/material/stepper';
import { BreakpointObserver } from '@angular/cdk/layout';
import { AuthService } from './service/auth.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { NotificationStateService } from '../notification/services/notification-state.service';
import { NotificationStreamService } from '../notification/services/notification-stream.service';
import { TokenService } from './service/token.service';

@Component({
  selector: 'app-auth',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatStepperModule,
    MatDatepickerModule,
  ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css',
})
export class AuthComponent implements OnInit {
  isLoginMode = signal(true);
  isLoading = signal(false);
  error = signal('');
  stepperOrientation = signal<'horizontal' | 'vertical'>('horizontal');
  private breakpointSub?: Subscription;

  private authService = inject(AuthService);
  router = inject(Router);
  breakPointObserver = inject(BreakpointObserver);
  private notificationStreamService = inject(NotificationStreamService);
  private notificationStateService = inject(NotificationStateService);
  private tokenService = inject(TokenService);

  loginForm = new FormGroup({
    username: new FormControl('', {
      validators: [Validators.required],
    }),
    password: new FormControl('', {
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });
  signupForm = new FormGroup({
    formArray: new FormArray<FormGroup>([
      new FormGroup({
        username: new FormControl('', [Validators.required]),
        password: new FormControl('', [
          Validators.required,
          Validators.minLength(6),
        ]),
      }),
      new FormGroup({
        firstName: new FormControl('', [Validators.required]),
        lastName: new FormControl('', [Validators.required]),
        birthdate: new FormControl('', [Validators.required]),
      }),
      new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        phoneNumber: new FormControl('', [Validators.required]),
      }),
    ]),
  });

  get formGroups() {
    return this.signupForm.controls.formArray.controls;
  }

  connectStream() {
    const token = this.tokenService.getAccessToken();
    if (token) {
      this.notificationStreamService.connect().subscribe({
        next: (notification) => {
          console.log('New SSE notification:', notification);
          this.notificationStateService.addNotification(notification);
        },
        error: (err) => console.error('SSE connection error', err),
      });
    }
  }

  onLogin() {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.error.set('');

    const { username, password } = this.loginForm.value;
    if (typeof username !== 'string' || typeof password !== 'string') {
      this.error.set('Username and password are required.');
      this.isLoading.set(false);
      return;
    }

    const authrequestDto = {
      username,
      password,
    };

    this.authService.login(authrequestDto).subscribe({
      next: () => {
        this.router.navigate(['/inside']);
        this.authService.setUserFromToken();
        this.connectStream();
      },
      error: (error) => {
        this.error.set(error?.message || 'Login failed');
        this.isLoading.set(false);
      },
    });
  }

  onSignUp() {
    if (this.signupForm.invalid) return;

    this.isLoading.set(true);
    this.error.set('');

    const [step1, step2, step3] = this.formGroups;
    if (
      !step1.get('username')?.value ||
      !step1.get('password')?.value ||
      !step2.get('firstName')?.value ||
      !step2.get('lastName')?.value ||
      !step2.get('birthdate')?.value ||
      !step3.get('email')?.value ||
      !step3.get('phoneNumber')?.value
    ) {
      this.error.set('All fields are required.');
      this.isLoading.set(false);

      return;
    }
    const rawBirthdate = step2.get('birthdate')?.value;
    const birthdate =
      rawBirthdate instanceof Date
        ? rawBirthdate.toISOString().split('T')[0]
        : '';

    const request = {
      username: step1.get('username')?.value,
      password: step1.get('password')?.value,
      firstName: step2.get('firstName')?.value,
      lastName: step2.get('lastName')?.value,
      birthdate,
      email: step3.get('email')?.value,
      phoneNumber: step3.get('phoneNumber')?.value,
      admin: false,
    };

    this.isLoading.set(true);
    this.error.set('');

    this.authService.register(request).subscribe({
      next: () => {
        this.router.navigate(['/inside']);
        this.authService.setUserFromToken();
        this.connectStream();
      },
      error: (error) => {
        this.error.set(error?.message || 'Signup failed');
        this.isLoading.set(false);
      },
    });
  }

  onSwitchmode() {
    this.isLoginMode.set(!this.isLoginMode());
  }

  ngOnInit(): void {
    this.breakpointSub = this.breakPointObserver
      .observe(['(max-width: 600px)'])
      .subscribe((result) => {
        this.stepperOrientation.set(result.matches ? 'vertical' : 'horizontal');
      });
  }
  ngOnDestroy() {
    this.breakpointSub?.unsubscribe();
  }
}
