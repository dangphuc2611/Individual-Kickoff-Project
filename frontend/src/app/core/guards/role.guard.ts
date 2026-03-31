import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.hasRole('TRUONG_PHONG') || authService.hasRole('CBCT')) {
    return true;
  }
  
  return router.createUrlTree(['/']);
};
