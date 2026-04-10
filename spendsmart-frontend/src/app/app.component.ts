import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { AlertService } from './services/alert.service';

/**
 * Root application component.
 * Renders the sidebar navigation when the user is logged in,
 * and the router outlet for the current page content.
 * Handles the global layout: sidebar + main content area.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html'
})
export class AppComponent {

  /** Application title */
  title = 'SpendSmart';

  /** Number of unread alerts for the navbar badge */
  unreadAlertsCount = 0;

  constructor(
    public authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {
    // Load unread alert count when user is logged in
    if (this.authService.isLoggedIn()) {
      this.loadAlertCount();
    }
  }

  /**
   * Load the count of unread alerts for the navbar badge.
   */
  loadAlertCount(): void {
    this.alertService.getUnreadAlerts().subscribe({
      next: (res) => {
        if (res.success) this.unreadAlertsCount = res.data.length;
      }
    });
  }

  /**
   * Log out the current user and redirect to the login page.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
