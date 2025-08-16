import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import { HeaderComponent } from './header/header.component';
import { authGuard, reverseAuthGuard } from './auth/guard/auth.guard';
import { UserUpdateComponent } from './user/user-update/user-update.component';
import { UserCreateComponent } from './user/user-create/user-create.component';
import { adminGuard } from './user/guards/admin.guard';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { DashboardComponent } from './Dashboard/dashboard.component';

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
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
  },
  {
    path: 'update/:id',
    component: UserUpdateComponent,
    canActivate: [authGuard],
  },
  {
    path: 'create',
    component: UserCreateComponent,
    canActivate: [authGuard, adminGuard],
  },
  {
    path: 'users/:id',
    component: UserProfileComponent,
  },
  {
    path: 'library',
    loadChildren: () =>
      import('./library/library.routes').then((m) => m.libraryRoutes),
    canActivate: [authGuard],
  },
  {
    path: 'career-package',
    loadChildren: () =>
      import('./career-package/career-package.routes').then(
        (m) => m.careerPackageRoutes
      ),
    canActivate: [authGuard],
  },
];
