import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import { AuthService } from '../services/auth';

export const farmerGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getUser();

  if (!user) {
    return router.createUrlTree(['/login']);
  }

  if (user.role !== 'FARMER') {
    return router.createUrlTree(['/']);
  }

  return true;
};