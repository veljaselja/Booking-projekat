import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: string;
  fullName: string;
  email: string;
  role: 'ADMIN' | 'HOST' | 'GUEST';
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, req).pipe(
      tap(res => {
        localStorage.setItem('userId', res.id);
        localStorage.setItem('role', res.role);
        localStorage.setItem('status', res.status);
        localStorage.setItem('email', res.email);
        localStorage.setItem('fullName', res.fullName);
      })
    );
  }

  logout(): void {
    localStorage.clear();
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('userId');
  }
}
