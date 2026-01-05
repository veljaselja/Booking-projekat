import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReservationRequest {
  houseId: string;
  dateFrom: string;   // YYYY-MM-DD
  dateTo: string;     // YYYY-MM-DD
  guestsCount: number;
}

export interface ReservationDto {
  id?: string;
  _id?: string;
  houseId: string;
  guestId?: string;
  dateFrom: string;
  dateTo: string;
  guestsCount: number;
  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  requestedAt?: string;
  decidedAt?: string | null;
  decidedByUserId?: string | null;
  decisionNote?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly baseUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  //  Guest šalje zahtev za rezervaciju
  requestReservation(guestId: string, payload: ReservationRequest): Observable<ReservationDto> {
    const headers = new HttpHeaders({ 'X-Guest-Id': guestId });

    return this.http.post<ReservationDto>(`${this.baseUrl}`, payload, { headers });
  }
}
