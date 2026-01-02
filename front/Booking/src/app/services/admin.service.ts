import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDto {
  id: string;
  fullName: string;
  email: string;
  role: 'ADMIN' | 'HOST' | 'GUEST';
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getPending(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.baseUrl}/users/pending`);
  }

  approve(adminId: string, userId: string): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.baseUrl}/users/${userId}/approve?adminId=${encodeURIComponent(adminId)}`, {});
  }

  disable(adminId: string, userId: string): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.baseUrl}/users/${userId}/disable?adminId=${encodeURIComponent(adminId)}`, {});
  }
}
