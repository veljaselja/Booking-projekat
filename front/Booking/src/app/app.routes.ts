import { Routes } from '@angular/router';
import { Login } from './login/login.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { HostHousesComponent } from './host-houses/host-houses.component';
import { BrowseHousesComponent } from './browse-houses/browse-houses.component';

import { adminGuard } from './guards/admin.guard';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: Login },

{ path: 'browse', component: BrowseHousesComponent, canActivate: [authGuard] },
  { path: 'host/houses', component: HostHousesComponent, canActivate: [authGuard] },

  // admin i dalje ima adminGuard + authGuard (authGuard može i da se izostavi jer adminGuard već proverava userId)
  { path: 'admin', component: AdminDashboardComponent, canActivate: [adminGuard] },

  { path: '**', redirectTo: '' },
];
