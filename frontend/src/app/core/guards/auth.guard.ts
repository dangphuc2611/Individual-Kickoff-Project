import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Kiểm tra token trong localStorage
  const token = typeof window !== 'undefined' ? localStorage.getItem('access_token') : null;

  if (token) {
    return true;
  }

  // Nếu chưa đăng nhập, đá về trang login
  return router.createUrlTree(['/login'], { 
    queryParams: { returnUrl: state.url } 
  });
};
