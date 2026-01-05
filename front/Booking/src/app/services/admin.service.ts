import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDto {
  id?: string;                  
  _id?: string;                 
  fullName: string;
  email: string;
  role?: 'ADMIN' | 'HOST' | 'GUEST';
  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';
  createdAt?: string | null;
  approvedAt?: string | null;
  approvedByAdminId?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getPending(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.baseUrl}/users/pending`);
  }

  approve(adminId: string, userId: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Admin-Id': adminId });
    return this.http.post(`${this.baseUrl}/users/${userId}/approve`, {}, { headers });
  }

  disable(adminId: string, userId: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Admin-Id': adminId });
    return this.http.post(`${this.baseUrl}/users/${userId}/disable`, {}, { headers });
  }
}
