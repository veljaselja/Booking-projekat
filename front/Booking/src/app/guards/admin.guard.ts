import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);

  const role = localStorage.getItem('role');     // "ADMIN" | "HOST" | "GUEST"
  const userId = localStorage.getItem('userId'); // postoji kad je ulogovan

  // nije ulogovan
  if (!userId) {
    router.navigate(['']);
    return false;
  }

  // nije admin
  if (role !== 'ADMIN') {
    router.navigate(['']); // ili '/dashboard' ako imaš
    return false;
  }

  return true;
};
