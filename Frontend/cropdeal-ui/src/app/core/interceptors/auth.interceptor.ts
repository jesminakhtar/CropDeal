import { HttpInterceptorFn } from '@angular/common/http';
import { API_BASE_URL } from '../config/api.config';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  if (!token || !req.url.startsWith(API_BASE_URL)) {
    return next(req);
  }

  const authorizationValue = token.startsWith('Bearer ')
    ? token
    : `Bearer ${token}`;

  const authenticatedRequest = req.clone({
    setHeaders: {
      Authorization: authorizationValue
    }
  });

  return next(authenticatedRequest);
};