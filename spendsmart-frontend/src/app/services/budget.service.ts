import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Budget } from '../models/budget.model';

/**
 * Service for budget operations.
 * All methods call the Spring Boot /api/budgets endpoints.
 * JWT authentication is handled automatically by the JwtInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class BudgetService {

  /** Base URL for budget API endpoints */
  private readonly budgetUrl = `${environment.apiUrl}/api/budgets`;

  constructor(private http: HttpClient) {}

  /**
   * Retrieve all budgets for a specific month.
   *
   * @param month the month in 'YYYY-MM' format (e.g. '2026-04')
   * @returns Observable with all budgets for the specified month
   */
  getBudgetsByMonth(month: string): Observable<ApiResponse<Budget[]>> {
    return this.http.get<ApiResponse<Budget[]>>(`${this.budgetUrl}/month/${month}`);
  }

  /**
   * Create a new monthly budget.
   *
   * @param budget the budget data to create (maxAmount, month, categoryId)
   * @returns Observable with the created budget
   */
  createBudget(budget: Budget): Observable<ApiResponse<Budget>> {
    return this.http.post<ApiResponse<Budget>>(this.budgetUrl, budget);
  }

  /**
   * Retrieve budgets with remaining amounts for a specific month.
   *
   * @param month the month in 'YYYY-MM' format
   * @returns Observable with budgets including remaining amount calculations
   */
  getRemainingBudgets(month: string): Observable<ApiResponse<Budget[]>> {
    return this.http.get<ApiResponse<Budget[]>>(`${this.budgetUrl}/remaining/${month}`);
  }
}
