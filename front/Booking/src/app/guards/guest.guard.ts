import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);

  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  if (!userId) {
    router.navigate(['']);
    return false;
  }

  if (role !== 'GUEST') {
    router.navigate(['']);
    return false;
  }

  return true;
};
