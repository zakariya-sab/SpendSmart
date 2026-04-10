import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../services/alert.service';
import { Alert } from '../../models/alert.model';

/**
 * Alerts component — displays and manages budget alerts.
 * Features:
 *   - List of all alerts with type badges (WARNING / EXCEEDED)
 *   - Unread alerts highlighted in orange
 *   - Mark individual alert as read button
 *   - Mark all as read button
 */
@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alerts.component.html'
})
export class AlertsComponent implements OnInit {

  /** All alerts for the current user (read and unread) */
  alerts: Alert[] = [];

  /** Whether data is loading */
  isLoading = false;

  /** Feedback message */
  message = '';

  constructor(private alertService: AlertService) {}

  /** Load all alerts when the component initializes */
  ngOnInit(): void {
    this.loadAlerts();
  }

  /**
   * Load all alerts (read and unread) from the API.
   */
  loadAlerts(): void {
    this.isLoading = true;
    this.alertService.getAllAlerts().subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) this.alerts = res.data;
      },
      error: () => { this.isLoading = false; }
    });
  }

  /**
   * Mark a single alert as read by its ID.
   *
   * @param id the ID of the alert to mark as read
   */
  markAsRead(id: number): void {
    this.alertService.markAsRead(id).subscribe({
      next: (res) => {
        if (res.success) {
          // Update the alert in the local list without reloading
          const alert = this.alerts.find(a => a.id === id);
          if (alert) alert.isRead = true;
        }
      }
    });
  }

  /**
   * Mark all alerts as read and refresh the list.
   */
  markAllAsRead(): void {
    this.alertService.markAllAsRead().subscribe({
      next: (res) => {
        if (res.success) {
          // Mark all alerts as read in the local list
          this.alerts.forEach(a => a.isRead = true);
          this.message = 'All alerts marked as read.';
          setTimeout(() => this.message = '', 3000);
        }
      }
    });
  }

  /**
   * Count of unread alerts for the header badge.
   *
   * @returns number of alerts where isRead is false
   */
  get unreadCount(): number {
    return this.alerts.filter(a => !a.isRead).length;
  }
}
