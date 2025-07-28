import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { AuthService } from './auth/service/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'edp-frontend';

  private authService = inject(AuthService);

  ngOnInit(): void {
    // TODO: search initialize before update and double request
    // this.authService.setUserFromToken().subscribe();
  }
}
