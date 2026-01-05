import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { ReservationService, ReservationRequest } from '../services/reservation.service';
import { HouseDto } from '../services/house.service';

export interface ReservationDialogData {
  house: HouseDto;
  defaultFrom?: Date | null;
  defaultTo?: Date | null;
  defaultGuests?: number | null;
}

@Component({
  selector: 'app-reservation-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './reservation-dialog.component.html',
  styleUrls: ['./reservation-dialog.component.css']
})
export class ReservationDialogComponent {

  form: FormGroup;

  errorMessage = '';
  successMessage = '';
  isSubmitting = false;

  guestId = localStorage.getItem('userId') || '';
  role = localStorage.getItem('role') || '';
  status = localStorage.getItem('status') || '';

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private dialogRef: MatDialogRef<ReservationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReservationDialogData
  ) {
    const g = data.defaultGuests && data.defaultGuests > 0 ? data.defaultGuests : 1;

    this.form = this.fb.group({
      dateFrom: [data.defaultFrom ?? null, Validators.required],
      dateTo: [data.defaultTo ?? null, Validators.required],
      guestsCount: [g, [Validators.required, Validators.min(1)]]
    });
  }

  private fmt(d: Date): string {
    return d.toISOString().slice(0, 10); // YYYY-MM-DD
  }

  submit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.guestId) {
      this.errorMessage = 'Nisi ulogovan.';
      return;
    }

    // Ako striktno želiš samo GUEST da rezerviše:
    if (this.role !== 'GUEST') {
      this.errorMessage = 'Samo GUEST može da kreira rezervaciju.';
      return;
    }

    if (this.status !== 'APPROVED') {
      this.errorMessage = 'Nalog mora biti odobren (APPROVED) da bi rezervisao.';
      return;
    }

    if (this.form.invalid) {
      this.errorMessage = 'Popuni datume i broj gostiju.';
      return;
    }

    const from: Date = this.form.value.dateFrom;
    const to: Date = this.form.value.dateTo;

    if (!(from instanceof Date) || !(to instanceof Date)) {
      this.errorMessage = 'Datumi nisu ispravni.';
      return;
    }

    if (from >= to) {
      this.errorMessage = 'Datum OD mora biti pre datuma DO.';
      return;
    }

    // maxGuests validacija (da bude lepo na frontu)
    const guestsCount: number = Number(this.form.value.guestsCount);
    if (guestsCount > (this.data.house.maxGuests || 1)) {
      this.errorMessage = `Maksimalno dozvoljeno gostiju za ovaj smeštaj: ${this.data.house.maxGuests}.`;
      return;
    }

    const payload: ReservationRequest = {
      houseId: (this.data.house.id || this.data.house._id || '') as string,
      dateFrom: this.fmt(from),
      dateTo: this.fmt(to),
      guestsCount
    };

    if (!payload.houseId) {
      this.errorMessage = 'Nedostaje houseId.';
      return;
    }

    this.isSubmitting = true;

    this.reservationService.requestReservation(this.guestId, payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = 'Zahtev poslat! Rezervacija je PENDING (čeka odobrenje).';
        setTimeout(() => this.dialogRef.close(true), 600);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err?.error?.message ?? 'Greška pri slanju zahteva.';
      }
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
