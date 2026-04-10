import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Alert } from '../models/alert.model';

/**
 * Service for financial alert operations.
 * All methods call the Spring Boot /api/alerts endpoints.
 * JWT authentication is handled automatically by the JwtInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class AlertService {

  /** Base URL for alert API endpoints */
  private readonly alertUrl = `${environment.apiUrl}/api/alerts`;

  constructor(private http: HttpClient) {}

  /**
   * Retrieve all unread alerts for the authenticated user.
   *
   * @returns Observable with unread alerts
   */
  getUnreadAlerts(): Observable<ApiResponse<Alert[]>> {
    return this.http.get<ApiResponse<Alert[]>>(this.alertUrl);
  }

  /**
   * Retrieve all alerts (read and unread) for the authenticated user.
   *
   * @returns Observable with all alerts
   */
  getAllAlerts(): Observable<ApiResponse<Alert[]>> {
    return this.http.get<ApiResponse<Alert[]>>(`${this.alertUrl}/all`);
  }

  /**
   * Mark a specific alert as read.
   *
   * @param id the ID of the alert to mark as read
   * @returns Observable with the updated alert
   */
  markAsRead(id: number): Observable<ApiResponse<Alert>> {
    return this.http.put<ApiResponse<Alert>>(`${this.alertUrl}/${id}/read`, {});
  }

  /**
   * Mark all unread alerts as read for the authenticated user.
   *
   * @returns Observable with the result
   */
  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.alertUrl}/read-all`, {});
  }
}
