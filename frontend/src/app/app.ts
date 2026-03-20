import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet } from '@angular/router';
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
      <app-navbar (menuClick)="toggleSidebar()"></app-navbar>

      <!-- Spacer for fixed topbar -->
      <div class="h-[72px]"></div>

      <div class="flex flex-1 overflow-hidden" style="height: calc(100vh - 72px);">
        
        <!-- Desktop Static Sidebar -->
        <div class="hidden md:block bg-white shadow-sm border-r border-slate-200 overflow-y-auto transition-all duration-300 z-40 shrink-0"
             [ngClass]="{'w-64': sidebarVisible, 'w-0 overflow-hidden opacity-0 p-0': !sidebarVisible}">
          <div class="p-1 w-64" *ngIf="sidebarVisible">
            <p-menu [model]="menuItems" [style]="{'border': 'none'}" styleClass="w-full border border-none bg-transparent"></p-menu>
          </div>
        </div>

        <!-- Mobile Drawer Sidebar -->
        <p-drawer [(visible)]="sidebarMobileVisible" [style]="{width: '260px'}" styleClass="md:hidden padding-1">
          <div class="p-2 pt-1">
             <!-- <div class="flex items-center gap-2 text-indigo-600 font-bold text-xl mb-6 ml-2">
                <i class="pi pi-spin pi-compass text-2xl"></i>
                <span>Kickoff<span class="text-slate-800">App</span></span>
             </div> -->
             <p-menu [model]="menuItems" [style]="{'border': 'none'}" styleClass="w-full border border-none bg-transparent"></p-menu>
          </div>
        </p-drawer>

        <!-- Main Content -->
        <div class="flex-1 overflow-y-auto bg-slate-50 transition-all min-w-0">
          <router-outlet></router-outlet>
        </div>
        
      </div>
    </div>
  `,
  styles: [`
    :host ::ng-deep .p-menu {
      padding: 0;
    }
  `]
})
export class App {
  sidebarVisible = true;
  sidebarMobileVisible = false;

  menuItems: MenuItem[] = [
    {
      label: 'MANAGEMENT',
      items: [
        { label: 'Tasks', icon: 'pi pi-check-square', routerLink: '/tasks' },
        { label: 'Users', icon: 'pi pi-users', routerLink: '/users' }
      ]
    }
  ];

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

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
