import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { HouseService, HouseDto } from '../services/house.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReservationDialogComponent } from '../reservation-dialog/reservation-dialog.component';


@Component({
  selector: 'app-browse-houses',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule
  ],
  templateUrl: './browse-houses.component.html',
  styleUrls: ['./browse-houses.component.css']
})
export class BrowseHousesComponent implements OnInit {

  houses: HouseDto[] = [];
  loading = false;
  error = '';
  role = localStorage.getItem('role') || '';
  status = localStorage.getItem('status') || '';
  isGuest = this.role === 'GUEST' && this.status === 'APPROVED';


  city = new FormControl<string>('');
  guests = new FormControl<number>(1);
  dateFrom = new FormControl<Date | null>(null);
  dateTo = new FormControl<Date | null>(null);

constructor(
  private houseService: HouseService,
  private auth: AuthService,
  private router: Router,
  private dialog: MatDialog
) {}

isHost = localStorage.getItem('role') === 'HOST';


  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    
this.houseService.search(
  this.city.value || undefined,
  this.guests.value || 1,
  this.dateFrom.value,
  this.dateTo.value
)      .subscribe({
        next: (data: HouseDto[]) => {
          this.houses = Array.isArray(data) ? [...data] : [];
          this.loading = false;
        },
        error: () => {
          this.error = 'Greška pri učitavanju smeštaja.';
          this.loading = false;
        }
      });
  }
  goToMyHouses(): void {
  this.router.navigate(['/host/houses']);
}

openCreateHouse(): void {
  alert('Create House dialog (dodajemo sledeće).');
}

logout(): void {
  this.auth.logout();
  this.router.navigate(['/']);
}
openReserve(house: HouseDto): void {
  this.dialog.open(ReservationDialogComponent, {
    width: '720px',
    data: {
      house,
      defaultFrom: this.dateFrom.value,
      defaultTo: this.dateTo.value,
      defaultGuests: this.guests.value || 1
    }
  }).afterClosed().subscribe((ok: boolean) => {
    if (ok) this.load();
  });
}

}
