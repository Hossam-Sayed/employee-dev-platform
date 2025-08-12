import { Routes } from '@angular/router';

export const careerPackageRoutes: Routes = [
  {
    path: '',
    redirectTo: 'templates',
    pathMatch: 'full',
  },
  {
    path: 'templates',
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./template/template-list/template-list.component').then(
            (m) => m.TemplateListComponent
          ),
      },
      {
        path: ':id',
        loadComponent: () =>
          import('./template/template-detail/template-detail.component').then(
            (m) => m.TemplateDetailComponent
          ),
      },
    ],
  },
  {
    path: 'my-package',
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./my-package/package-detail/package-detail.component').then(
            (m) => m.PackageDetailComponent
          ),
      },
      {
        path: 'sections/:sectionProgressId',
        loadComponent: () =>
          import(
            './my-package/package-section-detail/package-section-detail.component'
          ).then((m) => m.PackageSectionDetailComponent),
      },
    ],
  },
  {
    path: 'submissions',
    children: [
      {
        path: 'my-package/:submissionId',
        loadComponent: () =>
          import(
            './submission/submission-detail/submission-detail.component'
          ).then((m) => m.SubmissionDetailComponent),
      },
      {
        path: 'managed',
        loadComponent: () =>
          import('./submission/managed-submissions-list/managed-submissions-list.component').then(m => m.ManagedSubmissionsListComponent),
      },
      {
        path: 'action/:submissionId',
        loadComponent: () =>
          import('./submission/submission-action/submission-action.component').then(m => m.SubmissionActionComponent),
      },
    ],
  },
];
