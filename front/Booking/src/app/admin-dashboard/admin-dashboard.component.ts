import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  adminId = localStorage.getItem('userId') || ''; // privremeno

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.adminService.getPending().subscribe({
      next: (data) => { this.users = data; this.loading = false; },
      error: (err) => { this.error = err?.error?.message || 'Greška pri učitavanju'; this.loading = false; }
    });
  }

  approve(userId: string) {
    if (!this.adminId) { this.error = 'Nema adminId (uloguj admina).'; return; }
    this.adminService.approve(this.adminId, userId).subscribe({
      next: () => this.users = this.users.filter(u => u.id !== userId),
      error: (err) => this.error = err?.error?.message || 'Greška pri odobravanju'
    });
  }

  disable(userId: string) {
    if (!this.adminId) { this.error = 'Nema adminId (uloguj admina).'; return; }
    this.adminService.disable(this.adminId, userId).subscribe({
      next: () => this.users = this.users.filter(u => u.id !== userId),
      error: (err) => this.error = err?.error?.message || 'Greška pri uklanjanju'
    });
  }
}
