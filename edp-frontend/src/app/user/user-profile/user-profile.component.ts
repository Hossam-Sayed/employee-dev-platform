import { Component, inject, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UserResponse } from '../models/user-response.model';
import { UserService } from '../services/user.service';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [
    MatCardModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
  ],
})
export class UserProfileComponent implements OnInit {
  route = inject(ActivatedRoute);
  userService = inject(UserService);
  authService = inject(AuthService);

  user?: UserResponse;
  loading = true;
  error = '';
  userId = this.authService.getUserId();

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(id)) {
      this.error = 'Invalid user ID';
      this.loading = false;
      return;
    }

    this.userService.getUserById(id).subscribe({
      next: (data) => {
        this.user = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load user profile';
        this.loading = false;
      },
    });
  }
}
