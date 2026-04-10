import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Score } from '../models/score.model';

/**
 * Service for financial health score operations.
 * All methods call the Spring Boot /api/scores endpoints.
 * JWT authentication is handled automatically by the JwtInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class ScoreService {

  /** Base URL for score API endpoints */
  private readonly scoreUrl = `${environment.apiUrl}/api/scores`;

  constructor(private http: HttpClient) {}

  /**
   * Retrieve all historical financial health scores for the authenticated user.
   *
   * @returns Observable with all scores ordered newest first
   */
  getAllScores(): Observable<ApiResponse<Score[]>> {
    return this.http.get<ApiResponse<Score[]>>(this.scoreUrl);
  }

  /**
   * Retrieve the financial health score for the current month.
   *
   * @returns Observable with the current month's score
   */
  getCurrentScore(): Observable<ApiResponse<Score>> {
    return this.http.get<ApiResponse<Score>>(`${this.scoreUrl}/current`);
  }
}
