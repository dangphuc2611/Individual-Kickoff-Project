import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { DrawerModule } from 'primeng/drawer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, CommonModule, MenuModule, DrawerModule],
  template: `
    <div class="min-h-screen bg-slate-50 flex flex-col">
      <!-- Navbar (Hidden on Login) -->
      <app-navbar *ngIf="!isLoginPage" (menuClick)="toggleSidebar()"></app-navbar>

      <!-- Spacer for fixed topbar -->
      <div *ngIf="!isLoginPage" class="h-[72px]"></div>

      <div class="flex flex-1 overflow-hidden" style="height: calc(100vh - 72px);">
        
        <!-- Desktop Static Sidebar -->
        <div *ngIf="!isLoginPage" class="hidden md:block bg-white shadow-sm border-r border-slate-200 overflow-y-auto transition-all duration-300 z-40 shrink-0"
             [ngClass]="{'w-64': sidebarVisible, 'w-0 overflow-hidden opacity-0 p-0': !sidebarVisible}">
          <div class="p-4 w-64" *ngIf="sidebarVisible">
             <div class="text-xs font-semibold text-slate-400 mb-2 mt-2 uppercase tracking-widest pl-3">Menu</div>
             <p-menu [model]="menuItems" [style]="{'border': 'none'}" styleClass="w-full border border-none bg-transparent custom-menu"></p-menu>
          </div>
        </div>

        <!-- Mobile Drawer Sidebar -->
        <p-drawer *ngIf="!isLoginPage" [(visible)]="sidebarMobileVisible" [style]="{width: '260px'}" styleClass="md:hidden padding-1">
          <div class="p-2 pt-4">
             <div class="text-xs font-semibold text-slate-400 mb-2 mt-2 uppercase tracking-widest pl-3">Menu</div>
             <p-menu [model]="menuItems" [style]="{'border': 'none'}" styleClass="w-full border border-none bg-transparent custom-menu"></p-menu>
          </div>
        </p-drawer>

        <!-- Main Content (Full width on Login) -->
        <div class="flex-1 overflow-y-auto bg-slate-50 transition-all min-w-0 flex flex-col" [ngClass]="{'items-center justify-center': isLoginPage}">
          <router-outlet></router-outlet>
        </div>
        
      </div>
    </div>
  `,
  styles: [`
    :host ::ng-deep .custom-menu .p-menuitem-link {
       border-radius: 0.5rem;
       margin-bottom: 0.25rem;
       transition: all 0.2s ease;
    }
    :host ::ng-deep .custom-menu .p-menuitem-link:hover {
       background-color: #f1f5f9;
       color: #4f46e5;
    }
    :host ::ng-deep .custom-menu .p-menuitem-icon {
       margin-right: 0.75rem;
       color: #64748b;
    }
    :host ::ng-deep .custom-menu .p-menuitem-link:hover .p-menuitem-icon {
       color: #4f46e5;
    }
    :host ::ng-deep .p-menu {
      padding: 0;
    }
  `]
})
export class App {
  sidebarVisible = true;
  sidebarMobileVisible = false;
  isLoginPage = false;

  menuItems: MenuItem[] = [
    {
      label: 'QUẢN TRỊ HỆ THỐNG',
      items: [
        { label: 'Bảng Task', icon: 'pi pi-check-square', routerLink: '/tasks' },
        { label: 'Người dùng', icon: 'pi pi-users', routerLink: '/users' }
      ]
    },
    {
      label: 'ĐIỀU TRA (MOD-04)',
      items: [
        { label: 'Hồ sơ Điều tra', icon: 'pi pi-folder-open', routerLink: '/dieu-tra' },
        { label: 'Thông tin Hình sự', icon: 'pi pi-id-card', routerLink: '/hinh-su' },
        { label: 'An ninh Mạng', icon: 'pi pi-shield', routerLink: '/an-ninh-mang' }
      ]
    }
  ];

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private router: Router
  ) {
    this.router.events.subscribe(() => {
      this.isLoginPage = this.router.url.includes('/login');
    });
  }

  toggleSidebar() {
    if (isPlatformBrowser(this.platformId)) {
      if (window.innerWidth < 768) {
         this.sidebarMobileVisible = !this.sidebarMobileVisible;
      } else {
         this.sidebarVisible = !this.sidebarVisible;
      }
    } else {
      this.sidebarVisible = !this.sidebarVisible;
    }
  }
}
