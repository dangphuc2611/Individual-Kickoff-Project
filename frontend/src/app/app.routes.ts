import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  // --- Protected Routes ---
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: 'users',
        loadComponent: () => import('./pages/user-list/user-list.component').then((m) => m.UserListComponent),
      },
      {
        path: 'tasks',
        loadComponent: () => import('./pages/task-list/task-list.component').then((m) => m.TaskListComponent),
      },
      {
        path: 'tasks/:id',
        loadComponent: () => import('./pages/task-detail/task-detail.component').then((m) => m.TaskDetailComponent),
      },
      {
        path: 'dieu-tra',
        loadComponent: () => import('./pages/ho-so-dieu-tra/danh-sach/danh-sach.component').then((m) => m.DanhSachDieuTraComponent),
      },
      {
        path: 'dieu-tra/create',
        loadComponent: () => import('./pages/ho-so-dieu-tra/form/form-dieu-tra.component').then((m) => m.FormDieuTraComponent),
      },
      {
        path: 'dieu-tra/import',
        loadComponent: () => import('./pages/ho-so-dieu-tra/import/import-dieu-tra.component').then((m) => m.ImportDieuTraComponent),
      },
      {
        path: 'dieu-tra/:id',
        loadComponent: () => import('./pages/ho-so-dieu-tra/chi-tiet/chi-tiet.component').then((m) => m.ChiTietDieuTraComponent),
      },
      {
        path: 'dieu-tra/:id/edit',
        loadComponent: () => import('./pages/ho-so-dieu-tra/form/form-dieu-tra.component').then((m) => m.FormDieuTraComponent),
      },
      // HÌNH SỰ
      {
        path: 'hinh-su',
        loadComponent: () => import('./pages/thong-tin-hinh-su/danh-sach/danh-sach.component').then(m => m.DanhSachHinhSuComponent),
      },
      {
        path: 'hinh-su/create',
        loadComponent: () => import('./pages/thong-tin-hinh-su/form/form-hinh-su.component').then(m => m.FormHinhSuComponent),
      },
      {
        path: 'hinh-su/import',
        loadComponent: () => import('./pages/thong-tin-hinh-su/import/import-hinh-su.component').then(m => m.ImportHinhSuComponent),
      },
      {
        path: 'hinh-su/:id',
        loadComponent: () => import('./pages/thong-tin-hinh-su/chi-tiet/chi-tiet.component').then(m => m.ChiTietHinhSuComponent),
      },
      {
        path: 'hinh-su/:id/edit',
        loadComponent: () => import('./pages/thong-tin-hinh-su/form/form-hinh-su.component').then(m => m.FormHinhSuComponent),
      },
      // AN NINH MẠNG
      {
        path: 'an-ninh-mang',
        loadComponent: () => import('./pages/ho-so-an-ninh-mang/danh-sach/danh-sach.component').then(m => m.DanhSachAnNinhMangComponent),
      },
      {
        path: 'an-ninh-mang/create',
        loadComponent: () => import('./pages/ho-so-an-ninh-mang/form/form-an-ninh-mang.component').then(m => m.FormAnNinhMangComponent),
      },
      {
        path: 'an-ninh-mang/import',
        loadComponent: () => import('./pages/ho-so-an-ninh-mang/import/import-an-ninh-mang.component').then(m => m.ImportAnNinhMangComponent),
      },
      {
        path: 'an-ninh-mang/:id',
        loadComponent: () => import('./pages/ho-so-an-ninh-mang/chi-tiet/chi-tiet.component').then(m => m.ChiTietAnNinhMangComponent),
      },
      {
        path: 'an-ninh-mang/:id/edit',
        loadComponent: () => import('./pages/ho-so-an-ninh-mang/form/form-an-ninh-mang.component').then(m => m.FormAnNinhMangComponent),
      },
      {
        path: 'access-logs',
        loadComponent: () => import('./pages/access-log/access-log.component').then(m => m.AccessLogComponent),
        canActivate: [roleGuard]
      },
    ]
  },

  { path: '**', redirectTo: 'login' },
];
