import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';

import { UserService, RegisterRequest } from '../services/user.service';

@Component({
  selector: 'app-register-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css'] // ⬅️ styleUrls (množina!)
})
export class RegisterDialogComponent {

  form: FormGroup;

  errorMessage = '';
  successMessage = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dialogRef: MatDialogRef<RegisterDialogComponent>
  ) {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      passwordHash: ['', Validators.required],
      phone: [''],
      role: ['GUEST', Validators.required] // HOST ili GUEST
    });
  }

  submit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.errorMessage = 'Popuni sva obavezna polja.';
      return;
    }

    this.isSubmitting = true;

    const payload: RegisterRequest = {
      fullName: this.form.value.fullName,
      email: this.form.value.email,
      passwordHash: this.form.value.passwordHash,
      role: this.form.value.role
    };

    console.log('REGISTER PAYLOAD →', payload);

    this.userService.register(payload).subscribe({
      next: () => {
        this.successMessage =
          'Registracija uspešna! Nalog čeka odobrenje admina.';
        setTimeout(() => this.dialogRef.close(true), 800);
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message || 'Greška prilikom registracije.';
        this.isSubmitting = false;
      }
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
