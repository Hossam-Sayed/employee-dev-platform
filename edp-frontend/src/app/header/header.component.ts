import { Component, inject } from '@angular/core';
import { TokenService } from '../auth/service/token.service';
import { AuthService } from '../auth/service/auth.service';
import { LogoutRequestDto } from '../auth/model/logout-request.dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  tokenService = inject(TokenService);
  authService = inject(AuthService);
  router = inject(Router);
  onLogout() {
    const username = this.tokenService.getUsernameFromAccessToken();
    if (!username) {
      console.error('No valid access token found');
      return;
    }
    const logoutRequest: LogoutRequestDto = { username };
    this.authService.logout(logoutRequest).subscribe({
      next: () => this.router.navigate(['/auth']),
      error: (error) => console.error('Logout failed', error),
    });
  }
}
