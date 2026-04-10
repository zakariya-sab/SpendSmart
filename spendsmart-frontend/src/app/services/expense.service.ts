import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Expense } from '../models/expense.model';

/**
 * Service for expense CRUD operations.
 * All methods call the Spring Boot /api/expenses endpoints.
 * JWT authentication is handled automatically by the JwtInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  /** Base URL for expense API endpoints */
  private readonly expenseUrl = `${environment.apiUrl}/api/expenses`;

  constructor(private http: HttpClient) {}

  /**
   * Retrieve all expenses for the currently authenticated user.
   *
   * @returns Observable with all expenses
   */
  getAllExpenses(): Observable<ApiResponse<Expense[]>> {
    return this.http.get<ApiResponse<Expense[]>>(this.expenseUrl);
  }

  /**
   * Retrieve expenses filtered by month.
   *
   * @param month the month in 'YYYY-MM' format (e.g. '2026-04')
   * @returns Observable with expenses for the specified month
   */
  getExpensesByMonth(month: string): Observable<ApiResponse<Expense[]>> {
    return this.http.get<ApiResponse<Expense[]>>(`${this.expenseUrl}/month/${month}`);
  }

  /**
   * Add a new expense for the currently authenticated user.
   *
   * @param expense the expense data to create
   * @returns Observable with the created expense
   */
  addExpense(expense: Expense): Observable<ApiResponse<Expense>> {
    return this.http.post<ApiResponse<Expense>>(this.expenseUrl, expense);
  }

  /**
   * Delete an expense by its ID.
   *
   * @param id the ID of the expense to delete
   * @returns Observable with the deletion result
   */
  deleteExpense(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.expenseUrl}/${id}`);
  }
}
