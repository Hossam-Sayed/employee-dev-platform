import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../auth/guard/auth.guard';
import { MyMaterialsComponent } from './my-materials/my-materials.component';
import { AddMaterialComponent } from './add-material/add-material.component';
import { MaterialDetailsComponent } from './material-details/material-details.component';
import { MaterialHistoryComponent } from './material-history/material-history.component';
import { MyTagRequestsComponent } from './my-tag-requests/my-tag-requests.component';
import { PendingReviewsComponent } from '../pending-reviews/pending-reviews.component';

const routes: Routes = [
  {
    path: 'my-materials',
    component: MyMaterialsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'add-learning',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'add-blog',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'add-wiki',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'learning/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'blog/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'wiki/:materialId',
    component: MaterialDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'edit-learning/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'edit-blog/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'edit-wiki/:materialId',
    component: AddMaterialComponent,
    canActivate: [authGuard],
  },
  {
    path: 'history/:type/:materialId',
    component: MaterialHistoryComponent,
    canActivate: [authGuard],
  },
  {
    path: 'my-tag-requests',
    component: MyTagRequestsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'review-pending',
    component: PendingReviewsComponent,
    canActivate: [authGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LibraryRoutingModule {}
