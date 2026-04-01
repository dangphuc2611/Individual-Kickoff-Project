import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, ButtonModule, MenuModule],
  template: `
    <div class="shadow-sm fixed w-full top-0 z-50 bg-white/80 backdrop-blur-lg h-[72px] flex items-center px-4 justify-between border-b border-slate-200 transition-all">
      <div class="flex items-center gap-4">
        <!-- Hamburger Menu -->
        <p-button icon="pi pi-bars" (onClick)="onMenuClick()" variant="text" severity="secondary" [rounded]="true" styleClass="!text-slate-600 hover:!bg-slate-100 transition-colors"></p-button>
      </div>
      <div class="flex items-center gap-2">
        <div class="relative">
          <div (click)="menu.toggle($event)" class="w-9 h-9 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold ml-2 shadow-inner border border-indigo-200 cursor-pointer hover:shadow-md transition-shadow">
            {{ userInitial }}
          </div>
          <p-menu #menu [model]="userMenuItems" [popup]="true" appendTo="body"></p-menu>
        </div>
      </div>
    </div>
  `
})
export class NavbarComponent implements OnInit {
  @Output() menuClick = new EventEmitter<void>();

  userInitial = 'A';
  userMenuItems: MenuItem[] = [];

  isManager = false;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.getCurrentUser().subscribe(user => {
      if (user) {
        if (user.email) {
          this.userInitial = user.email.charAt(0).toUpperCase();
        }
        this.isManager = this.authService.hasRole('TRUONG_PHONG') || this.authService.hasRole('CBCT');
        
        this.userMenuItems = [
          {
            label: user.email,
            items: [
              {
                label: `${user.role}`,
                icon: 'pi pi-id-card',
                disabled: true,
                styleClass: 'font-semibold text-indigo-600'
              },
              {
                separator: true
              },
              {
                label: 'Hồ sơ cá nhân',
                icon: 'pi pi-user'
              },
              {
                label: 'Đổi mật khẩu',
                icon: 'pi pi-key'
              },
              {
                separator: true
              },
              {
                label: 'Đăng xuất',
                icon: 'pi pi-sign-out',
                command: () => this.onLogout()
              }
            ]
          }
        ];
      }
    });
  }

  onLogout() {
    console.log('Initiating logout from Navbar');
    this.authService.logout();
  }

  onMenuClick() {
    this.menuClick.emit();
  }
}
