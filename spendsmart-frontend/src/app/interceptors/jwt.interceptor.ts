import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * HTTP interceptor that automatically attaches the JWT token to every outgoing HTTP request.
 * Adds the 'Authorization: Bearer {token}' header required by the Spring Boot API.
 * If no token is present (unauthenticated), the request is sent unchanged.
 *
 * @param req     the outgoing HTTP request
 * @param next    the next handler in the interceptor chain
 * @returns the modified request with Authorization header, or the original if no token
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    // Clone the request and add the Authorization header with the JWT token
    const authRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authRequest);
  }

  // No token — pass the request through unchanged (for public endpoints like /api/auth/**)
  return next(req);
};
