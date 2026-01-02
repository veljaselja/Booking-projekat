import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  fullName = localStorage.getItem('fullName') ?? '';
  role = localStorage.getItem('role') ?? '';
  status = localStorage.getItem('status') ?? '';
}
