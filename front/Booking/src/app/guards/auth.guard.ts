import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const userId = localStorage.getItem('userId');

  if (!userId) {
    router.navigate(['']); // login
    return false;
  }

  return true;
};
