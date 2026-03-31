import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  // Lấy token từ localStorage (chạy an toàn trên Browser, check SSR nếu cần)
  let token = null;
  if (typeof window !== 'undefined' && window.localStorage) {
    token = localStorage.getItem('access_token');
  }

  // Clone request và đính kèm JWT Token vào header Authorization
  let clonedReq = req;
  if (token) {
    clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Handle errors (401 Unauthorized, 403 Forbidden)
  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Hết hạn token hoặc không hợp lệ -> Xóa và đẩy về Login
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.removeItem('access_token');
        }
        router.navigate(['/login']);
      } else if (error.status === 403) {
        // Forbidden -> Log cảnh báo và hiển thị toast/message (Ở đây dùng console/window alert tạm)
        console.error('Không có quyền truy cập !', error);
      }
      return throwError(() => error);
    })
  );
};
