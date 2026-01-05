import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AuthService, LoginResponse } from '../services/auth.service';
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
    MatDialogModule,
    MatCardModule       

  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class Login implements OnInit {

  form!: FormGroup;
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const role = localStorage.getItem('role');
const userId = localStorage.getItem('userId');

if (userId) {
  if (role === 'ADMIN') this.router.navigate(['/admin']);
  else this.router.navigate(['/browse']); // HOST i GUEST
}


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
  // i host i guest da idu na pretragu
  this.router.navigate(['/browse']);
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
      width: '520px',
      disableClose: true
    });
  }
}
