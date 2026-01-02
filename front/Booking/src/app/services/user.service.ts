import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegisterRequest {
  fullName: string;
  email: string;
  passwordHash: string;
 
  role: 'HOST' | 'GUEST';
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly baseUrl = 'http://localhost:8080/api/users';

  private readonly jsonHeaders = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private http: HttpClient) {}

  register(payload: RegisterRequest): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/register`,
      payload,
      { headers: this.jsonHeaders }
    );
  }
}
