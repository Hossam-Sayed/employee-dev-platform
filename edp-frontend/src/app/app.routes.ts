import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import { HeaderComponent } from './header/header.component';
import { authGuard, reverseAuthGuard } from './auth/guard/auth.guard';
import { UserUpdateComponent } from './user/user-update/user-update.component';
import { UserCreateComponent } from './user/user-create/user-create.component';
import { TemplateListComponent } from './career-package/template/template-list/template-list.component';
import { adminGuard } from './user/guards/admin.guard';

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
    path: 'inside',
    component: HeaderComponent,
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
    path: 'library',
    loadChildren: () =>
      import('./library/library.module').then((m) => m.LibraryModule),
    canActivate: [authGuard],
  },
  {
    path: 'templates',
    component: TemplateListComponent,
  },
];
