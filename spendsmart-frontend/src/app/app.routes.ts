import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

/**
 * Application routing configuration.
 * Public routes: /login, /register (no authentication required)
 * Protected routes: all others — require valid JWT token (authGuard)
 * Default redirect: / → /dashboard
 */
export const routes: Routes = [
  /** Redirect root path to dashboard */
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  /** Public authentication routes — no guard needed */
  {
    path: 'login',
    loadComponent: () =>
      import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/auth/register/register.component').then(m => m.RegisterComponent)
  },

  /** Protected routes — require authentication via authGuard */
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'expenses',
    loadComponent: () =>
      import('./components/expenses/expenses.component').then(m => m.ExpensesComponent),
    canActivate: [authGuard]
  },
  {
    path: 'budgets',
    loadComponent: () =>
      import('./components/budgets/budgets.component').then(m => m.BudgetsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'alerts',
    loadComponent: () =>
      import('./components/alerts/alerts.component').then(m => m.AlertsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'statistics',
    loadComponent: () =>
      import('./components/statistics/statistics.component').then(m => m.StatisticsComponent),
    canActivate: [authGuard]
  },

  /** Fallback — redirect unknown routes to dashboard */
  { path: '**', redirectTo: 'dashboard' }
];
