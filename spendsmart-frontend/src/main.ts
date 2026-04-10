import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

/**
 * Application bootstrap — starts the Angular application.
 * Mounts the AppComponent as the root component with the provided configuration.
 */
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error('Failed to bootstrap SpendSmart application:', err));
