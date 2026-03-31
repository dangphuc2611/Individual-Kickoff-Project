import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { PasswordModule } from 'primeng/password';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    ButtonModule, 
    InputTextModule, 
    CheckboxModule, 
    PasswordModule,
    ToastModule
  ],
  providers: [MessageService],
  template: `
    <div class="h-screen bg-slate-50 flex flex-col justify-center items-center py-12 sm:px-6 lg:px-8">
      <p-toast></p-toast>
      <div class="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <!-- Logo Icon (Shield) -->
        <div class="mx-auto h-16 w-16 bg-blue-600 rounded-2xl shadow-lg flex items-center justify-center mb-6">
          <i class="pi pi-shield text-3xl text-white"></i>
        </div>
        
        <h2 class="text-center text-3xl font-extrabold text-slate-800 tracking-tight">
          Hệ thống Quản lý
        </h2>
        <p class="mt-2 text-center text-sm text-slate-500 max-w-sm mx-auto">
          Công tác Bảo vệ An ninh Quân đội (MOD-04)
        </p>
      </div>

      <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div class="bg-white py-8 px-6 shadow-xl border border-slate-100 rounded-2xl sm:px-10">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <!-- Email -->
            <div class="flex flex-col gap-2">
              <label for="email" class="block text-sm font-medium text-slate-700">Tài khoản Email</label>
              <div class="mt-1">
                <input 
                  id="email" 
                  type="email" 
                  pInputText 
                  formControlName="email" 
                  [ngClass]="{'ng-invalid ng-dirty': submitted && f['email'].errors}"
                  class="w-full" 
                  placeholder="admin@test.com" />
              </div>
              <small *ngIf="submitted && f['email'].errors?.['required']" class="text-red-500">Email không được để trống.</small>
              <small *ngIf="submitted && f['email'].errors?.['email']" class="text-red-500">Sai định dạng Email.</small>
            </div>

            <!-- Password -->
            <div class="flex flex-col gap-2">
              <label for="password" class="block text-sm font-medium text-slate-700">Mật khẩu</label>
              <div class="mt-1">
                <p-password 
                  id="password" 
                  [toggleMask]="true" 
                  [feedback]="false"
                  formControlName="password" 
                  [ngClass]="{'ng-invalid ng-dirty': submitted && f['password'].errors}"
                  styleClass="w-full"
                  [inputStyle]="{'width':'100%'}"
                  placeholder="••••••••"></p-password>
              </div>
              <small *ngIf="submitted && f['password'].errors?.['required']" class="text-red-500">Mật khẩu không được để trống.</small>
            </div>

            <!-- Remember me & Forgot -->
            <div class="flex items-center justify-between mt-4">
              <div class="flex items-center">
                <p-checkbox [binary]="true" inputId="rememberMe" formControlName="rememberMe"></p-checkbox>
                <label for="rememberMe" class="ml-2 block text-sm text-slate-700">
                  Ghi nhớ đăng nhập
                </label>
              </div>

              <div class="text-sm">
                <a href="#" class="font-medium text-blue-600 hover:text-blue-500">
                  Quên mật khẩu?
                </a>
              </div>
            </div>

            <div>
              <p-button 
                type="submit" 
                label="Đăng nhập hệ thống" 
                [loading]="loading" 
                styleClass="w-full" 
                size="large">
              </p-button>
            </div>
            
          </form>
          
          <!-- Note -->
          <div class="mt-6 text-center text-xs text-slate-500">
            Đây là hệ thống nội bộ. Mọi hành vi truy cập trái phép đều được ghi log.
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      rememberMe: [false]
    });
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

    // Stop if invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.login({ email: this.f['email'].value, password: this.f['password'].value })
      .subscribe({
        next: (res) => {
          this.loading = false;
          // Navigate to main list
          this.router.navigate(['/tasks']);
        },
        error: (err) => {
          this.loading = false;
          this.messageService.add({severity:'error', summary:'Lỗi đăng nhập', detail:'Thông tin tài khoản hoặc mật khẩu không chính xác.'});
        }
      });
  }
}
