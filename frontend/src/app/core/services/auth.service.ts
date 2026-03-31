import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse, UserPayload } from '../models/auth.model';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject = new BehaviorSubject<UserPayload | null>(null);

  constructor(private http: HttpClient, private router: Router) {
    this.initToken();
  }

  // Khởi tạo từ localStorage
  private initToken() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (token) {
        this.currentUserSubject.next(this.decodeToken(token));
      }
    }
  }

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem('access_token', response.token);
          this.currentUserSubject.next(this.decodeToken(response.token));
        }
      })
    );
  }

  logout() {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem('access_token');
    }
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getCurrentUser(): Observable<UserPayload | null> {
    return this.currentUserSubject.asObservable();
  }
  
  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.role?.toUpperCase() === role.toUpperCase() : false;
  }

  // Giả lập đọc base64 JWT Payload
  private decodeToken(token: string): UserPayload | null {
    try {
      const payloadBase64 = token.split('.')[1];
      const decodedJson = atob(payloadBase64);
      return JSON.parse(decodedJson) as UserPayload;
    } catch (e) {
      console.error('Invalid token format');
      return null;
    }
  }
}
