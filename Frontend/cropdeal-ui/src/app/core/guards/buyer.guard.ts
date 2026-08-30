import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';
import { AuthService } from '../services/auth-service';

export const buyerGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getUser();

  if (!user) {
    return router.createUrlTree(['/login']);
  }

  if (user.role !== 'DEALER') {
    return router.createUrlTree(['/']);
  }

  return true;
};