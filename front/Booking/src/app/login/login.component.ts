import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AuthService } from '../services/auth.service';
import { RegisterDialogComponent } from '../register-dialog/register-dialog.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class Login {

  form!: FormGroup;   // 👈 samo deklaracija
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) {
    // 👇 INICIJALIZACIJA IDE OVDE
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  submit(): void {
    this.errorMessage = '';

    if (this.form.invalid) {
      this.errorMessage = 'Unesi email i lozinku.';
      return;
    }

    this.isSubmitting = true;

    this.auth.login(this.form.getRawValue()).subscribe({
      next: (res) => {
        this.isSubmitting = false;

        if (res.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.errorMessage = `Ulogovan si kao ${res.role}.`;
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err?.error?.message ?? 'Greška pri login-u.';
      }
    });
  }

  openRegister(): void {
    this.dialog.open(RegisterDialogComponent, {
      width: '520px'
    });
  }
}
