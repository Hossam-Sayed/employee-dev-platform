import { Component, inject } from '@angular/core';
import { TokenService } from '../auth/service/token.service';
import { AuthService } from '../auth/service/auth.service';
import { LogoutRequestDto } from '../auth/model/logout-request.dto';
import { Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatMenuModule,MatIcon],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  tokenService = inject(TokenService);
  authService = inject(AuthService);
  router = inject(Router);

  goTo(path: string) {
        this.router.navigate([path]);
  }

  onLogout() {
    const username = this.tokenService.getPayload()?.sub;
    if (!username) {
      console.error('No valid access token found');
      return;
    }
    const logoutRequest: LogoutRequestDto = { username };
    this.authService.logout(logoutRequest).subscribe({
      next: () => {
        this.router.navigate(['/auth']);
        this.tokenService.clearTokens();
      },
      error: (error) => console.error('Logout failed', error),
    });
  }
}
