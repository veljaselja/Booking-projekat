import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';

import { AdminService, UserDto } from '../services/admin.service';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  users: UserDto[] = [];
  loading = false;
  error = '';
  success = '';

  adminId = localStorage.getItem('userId') || '';

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.load();
  }

  private getUserId(u: UserDto): string {
    return (u.id || u._id || '').trim();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.success = '';

    this.adminService.getPending()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges(); // ✅ garantuje refresh UI
      }))
      .subscribe({
        next: (data) => {
          // ✅ uvek nova referenca niza (pomaže render)
          this.users = Array.isArray(data) ? [...data] : [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = err?.error?.message || err?.message || 'Greška pri učitavanju.';
          this.cdr.detectChanges();
        }
      });
  }

  approve(user: UserDto): void {
    const userId = this.getUserId(user);
    if (!userId) { this.error = 'Nedostaje user id.'; return; }
    if (!this.adminId) { this.error = 'Nema adminId (uloguj admina).'; return; }

    this.error = '';
    this.success = '';

    this.adminService.approve(this.adminId, userId).subscribe({
      next: () => {
        this.users = this.users.filter(u => this.getUserId(u) !== userId);
        this.success = 'Korisnik je odobren.';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Greška pri odobravanju.';
        this.cdr.detectChanges();
      }
    });
  }

  disable(user: UserDto): void {
    const userId = this.getUserId(user);
    if (!userId) { this.error = 'Nedostaje user id.'; return; }
    if (!this.adminId) { this.error = 'Nema adminId (uloguj admina).'; return; }

    this.error = '';
    this.success = '';

    this.adminService.disable(this.adminId, userId).subscribe({
      next: () => {
        this.users = this.users.filter(u => this.getUserId(u) !== userId);
        this.success = 'Korisnik je uklonjen/disable.';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Greška pri uklanjanju.';
        this.cdr.detectChanges();
      }
    });
  }

  logout(): void {
    localStorage.clear();
    location.href = '/';
  }
}
