import { Routes } from '@angular/router';
import { Login } from './login/login.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'admin', component: AdminDashboardComponent },
];
