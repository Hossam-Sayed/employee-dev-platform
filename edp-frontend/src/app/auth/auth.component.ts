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
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { BreakpointObserver } from '@angular/cdk/layout';

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

  router = inject(Router);
  breakPointObserver = inject(BreakpointObserver);

  loginForm = new FormGroup({
    email: new FormControl('', {
      validators: [Validators.email, Validators.required],
    }),
    password: new FormControl('', {
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });
  signupForm = new FormGroup({
    formArray: new FormArray<FormGroup>([
      new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [
          Validators.required,
          Validators.minLength(6),
        ]),
      }),
      new FormGroup({
        firstName: new FormControl('', [Validators.required]),
        lastName: new FormControl('', [Validators.required]),
      }),
      new FormGroup({
        phone: new FormControl('', [Validators.required]),
        address: new FormControl('', [Validators.required]),
      }),
    ]),
  });

  get formGroups() {
    return this.signupForm.controls.formArray.controls;
  }

  onLogin() {
  }
  onSignUp() {

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
