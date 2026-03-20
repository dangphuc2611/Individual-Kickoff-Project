import { Component, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule, ButtonModule],
  template: `
    <div class="shadow-sm fixed w-full top-0 z-50 bg-white h-[72px] flex items-center px-4 justify-between border-b border-slate-200">
      <div class="flex items-center gap-4">
        <!-- Hamburger Menu -->
        <p-button icon="pi pi-bars" (onClick)="onMenuClick()" variant="text" severity="secondary" [rounded]="true" styleClass="!text-slate-600 hover:!bg-slate-100"></p-button>
        <!-- Logo -->
        <!-- <div class="flex items-center gap-2 text-indigo-600 font-bold text-xl cursor-pointer ml-2" routerLink="/">
          <i class="pi pi-spin pi-compass text-2xl"></i>
          <span>Kickoff<span class="text-slate-800">App</span></span>
        </div> -->
      </div>
      <div class="flex items-center gap-2">
        <p-button icon="pi pi-calendar" variant="text" severity="secondary" [rounded]="true" styleClass="!text-slate-600"></p-button>
        <p-button icon="pi pi-envelope" variant="text" severity="secondary" [rounded]="true" styleClass="!text-slate-600"></p-button>
        <p-button icon="pi pi-user" variant="text" severity="secondary" [rounded]="true" styleClass="!text-slate-600"></p-button>
      </div>
    </div>
  `
})
export class NavbarComponent {
  @Output() menuClick = new EventEmitter<void>();

  onMenuClick() {
    this.menuClick.emit();
  }
}
