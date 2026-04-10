import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './interceptors/jwt.interceptor';

/**
 * Root application configuration.
 * Registers the router with the app's route definitions,
 * and provides the HttpClient with the JWT interceptor
 * that adds Authorization headers to all API requests.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    /** Configure Angular Router with the application routes */
    provideRouter(routes),

    /** Provide HttpClient with the JWT interceptor for automatic token injection */
    provideHttpClient(withInterceptors([jwtInterceptor]))
  ]
};
