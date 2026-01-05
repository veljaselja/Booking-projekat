import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HouseDto {
  id?: string;
  _id?: string;
  hostId?: string;

  title: string;
  description?: string;

  city: string;
  address?: string;

  maxGuests: number;
  pricePerNight: number;

  imageUrls?: string[];

  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';
  createdAt?: string;
  approvedAt?: string | null;
  approvedByAdminId?: string | null;
}

export interface CreateHouseRequest {
  title: string;
  description?: string;
  city: string;
  address?: string;
  maxGuests: number;
  pricePerNight: number;
  imageUrls?: string[];
}

@Injectable({ providedIn: 'root' })
export class HouseService {
  private readonly baseUrl = 'http://localhost:8080/api/houses';

  constructor(private http: HttpClient) {}

  browse(city?: string): Observable<HouseDto[]> {
    let params = new HttpParams();
    if (city && city.trim()) params = params.set('city', city.trim());
    return this.http.get<HouseDto[]>(this.baseUrl, { params });
  }

  // ✅ NEW: search dostupnih kuća
  // šalje parametre: city, guests, from, to
  search(city?: string, guests?: number, from?: Date | null, to?: Date | null): Observable<HouseDto[]> {
    let params = new HttpParams();

    if (city && city.trim()) params = params.set('city', city.trim());
    if (guests && guests > 0) params = params.set('guests', String(guests));

    const fmt = (d: Date) => d.toISOString().slice(0, 10); // YYYY-MM-DD

    if (from) params = params.set('from', fmt(from));
    if (to) params = params.set('to', fmt(to));

    return this.http.get<HouseDto[]>(`${this.baseUrl}/search`, { params });
  }

  myHouses(hostId: string): Observable<HouseDto[]> {
    const headers = new HttpHeaders({ 'X-Host-Id': hostId });
    return this.http.get<HouseDto[]>(`${this.baseUrl}/mine`, { headers });
  }

  create(hostId: string, payload: CreateHouseRequest): Observable<HouseDto> {
    const headers = new HttpHeaders({ 'X-Host-Id': hostId });
    return this.http.post<HouseDto>(this.baseUrl, payload, { headers });
  }

  update(hostId: string, houseId: string, patch: Partial<CreateHouseRequest>): Observable<HouseDto> {
    const headers = new HttpHeaders({ 'X-Host-Id': hostId });
    return this.http.put<HouseDto>(`${this.baseUrl}/${houseId}`, patch, { headers });
  }

  delete(hostId: string, houseId: string): Observable<void> {
    const headers = new HttpHeaders({ 'X-Host-Id': hostId });
    return this.http.delete<void>(`${this.baseUrl}/${houseId}`, { headers });
  }
}
