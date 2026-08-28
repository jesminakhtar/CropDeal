import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/pages/home/home')
        .then(m => m.HomeComponent)
  },

  {
    path: '**',
    redirectTo: ''
  }
];