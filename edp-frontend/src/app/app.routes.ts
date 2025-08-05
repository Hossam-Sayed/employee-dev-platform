import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import { HeaderComponent } from './header/header.component';
import { authGuard, reverseAuthGuard } from './auth/guard/auth.guard';
import { UserUpdateComponent } from './user/user-update/user-update.component';
import { UserCreateComponent } from './user/user-create/user-create.component';
import { adminGuard } from './user/guards/admin.guard';
import { MyMaterialsComponent } from './library/my-materials/my-materials.component';
import { AddMaterialComponent } from './library/add-material/add-material.component';
import { MaterialDetailsComponent } from './library/material-details/material-details.component';
import { MaterialHistoryComponent } from './library/material-history/material-history.component';

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
    path: 'library/my-materials',
    component: MyMaterialsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/add-learning',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/add-blog',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/add-wiki',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/learning/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/blog/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/wiki/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/edit-learning/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/edit-blog/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/edit-wiki/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'library/history/:type/:materialId',
    component: MaterialHistoryComponent,
    canActivate: [authGuard],
  },
];
