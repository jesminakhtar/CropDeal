import { Routes } from '@angular/router';
import { farmerGuard } from './core/guards/farmer.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/pages/home/home')
        .then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login')
        .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/register/register')
        .then(m => m.RegisterComponent)
  },
  {
    path: 'farmer/dashboard',
    canActivate: [farmerGuard],
    loadComponent: () =>
      import('./features/farmer/pages/dashboard/dashboard')
        .then(m => m.DashboardComponent)
  },
  {
    path: '**',
    redirectTo: ''
  },
];