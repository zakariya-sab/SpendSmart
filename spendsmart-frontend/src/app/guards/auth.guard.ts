import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Route guard that protects routes requiring authentication.
 * If the user is not logged in (no valid JWT token), they are redirected to /login.
 * Applied to all protected routes in the app router configuration.
 *
 * @returns true if the user is authenticated, or a UrlTree redirect to /login
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    // User has a valid JWT token — allow access to the route
    return true;
  }

  // No valid token — redirect to the login page
  return router.createUrlTree(['/login']);
};
