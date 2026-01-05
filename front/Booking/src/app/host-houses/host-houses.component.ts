import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormControl, FormGroup } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

import { HouseService, HouseDto, CreateHouseRequest } from '../services/house.service';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

type HostHouseForm = FormGroup<{
  title: FormControl<string>;
  description: FormControl<string>;
  city: FormControl<string>;
  address: FormControl<string>;
  maxGuests: FormControl<number>;
  pricePerNight: FormControl<number>;
  imageUrlsText: FormControl<string>;
}>;

@Component({
  selector: 'app-host-houses',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './host-houses.component.html',
  styleUrls: ['./host-houses.component.css'],
})
export class HostHousesComponent implements OnInit {

  houses: HouseDto[] = [];
  loading = false;
  error = '';
  success = '';

  hostId = localStorage.getItem('userId') || '';

  form!: HostHouseForm;

  constructor(
    private fb: FormBuilder,
    private houseService: HouseService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      title: this.fb.nonNullable.control('', [Validators.required]),
      description: this.fb.nonNullable.control(''),
      city: this.fb.nonNullable.control('', [Validators.required]),
      address: this.fb.nonNullable.control(''),
      maxGuests: this.fb.nonNullable.control(1, [Validators.required, Validators.min(1)]),
      pricePerNight: this.fb.nonNullable.control(1, [Validators.required, Validators.min(1)]),
      imageUrlsText: this.fb.nonNullable.control('')
    });

    this.load();
  }

  load(): void {
    if (!this.hostId) {
      this.error = 'Nema hostId (uloguj HOST).';
      return;
    }

    this.loading = true;
    this.error = '';

    this.houseService.myHouses(this.hostId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (data: HouseDto[]) => {
          this.houses = Array.isArray(data) ? [...data] : [];
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message || err.message;
          this.error = msg || 'Greška pri učitavanju smeštaja.';
        }
      });
  }

  submit(): void {
    this.error = '';
    this.success = '';

    if (!this.hostId) {
      this.error = 'Nema hostId (uloguj HOST).';
      return;
    }

    if (this.form.invalid) {
      this.error = 'Popuni obavezna polja.';
      return;
    }

    const v = this.form.getRawValue();

    const urlsRaw = (v.imageUrlsText || '').trim();
    const imageUrls = urlsRaw
      ? urlsRaw
          .split(',')
          .map((s: string) => s.trim())
          .filter((s: string) => s.length > 0)
      : [];

    const payload: CreateHouseRequest = {
      title: v.title,
      description: v.description || '',
      city: v.city,
      address: v.address || '',
      maxGuests: Number(v.maxGuests),
      pricePerNight: Number(v.pricePerNight),
      imageUrls
    };

    this.loading = true;

    this.houseService.create(this.hostId, payload)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.success = 'Smeštaj dodat (ide na odobrenje).';

          // reset forma na default vrednosti
          this.form.reset({
            title: '',
            description: '',
            city: '',
            address: '',
            maxGuests: 1,
            pricePerNight: 1,
            imageUrlsText: ''
          });

          this.load();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message || err.message;
          this.error = msg || 'Greška pri dodavanju smeštaja.';
        }
      });
  }
}
