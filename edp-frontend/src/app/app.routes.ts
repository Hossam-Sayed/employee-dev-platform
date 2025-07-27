import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import { HeaderComponent } from './header/header.component';
import { authGuard, reverseAuthGuard } from './auth/guard/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth',
    pathMatch: 'full',
  },
  {
    path: 'auth',
    component: AuthComponent,
    canActivate: [reverseAuthGuard],
  },
  {
    path:'inside',
    component: HeaderComponent,
    canActivate:[authGuard],
  }

];
