import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ScoreService } from '../../services/score.service';
import { ExpenseService } from '../../services/expense.service';
import { BudgetService } from '../../services/budget.service';
import { AlertService } from '../../services/alert.service';
import { Score } from '../../models/score.model';
import { Expense } from '../../models/expense.model';
import { Budget } from '../../models/budget.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/api-response.model';

/**
 * Dashboard component — the main landing page after login.
 * Shows:
 *   - Current month financial health score with grade and progress bar
 *   - Budget cards with remaining amounts and progress bars
 *   - Last 5 expenses table
 *   - Unread alerts count badge
 *   - ML prediction for next month spending
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  /** Current month's financial health score */
  currentScore: Score | null = null;

  /** All budgets for the current month */
  budgets: Budget[] = [];

  /** Last 5 expenses for the quick overview table */
  recentExpenses: Expense[] = [];

  /** Number of unread alerts (shown as badge) */
  unreadAlertsCount = 0;

  /** ML prediction for next month's spending */
  prediction: { predicted_amount: number; trend: string } | null = null;

  /** Whether data is still loading */
  isLoading = true;

  /** Current month in 'YYYY-MM' format */
  currentMonth: string;

  constructor(
    private scoreService: ScoreService,
    private expenseService: ExpenseService,
    private budgetService: BudgetService,
    private alertService: AlertService,
    private http: HttpClient
  ) {
    // Calculate the current month string in 'YYYY-MM' format
    const now = new Date();
    this.currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  }

  /**
   * Load all dashboard data on component initialization.
   * Makes parallel API calls for score, budgets, expenses, alerts, and ML prediction.
   */
  ngOnInit(): void {
    this.loadDashboardData();
  }

  /**
   * Load all dashboard data from the various API endpoints.
   * Sets loading state and populates component properties.
   */
  loadDashboardData(): void {
    this.isLoading = true;

    // Load current month's financial health score
    this.scoreService.getCurrentScore().subscribe({
      next: (res) => {
        if (res.success) this.currentScore = res.data;
      }
    });

    // Load budgets for current month
    this.budgetService.getBudgetsByMonth(this.currentMonth).subscribe({
      next: (res) => {
        if (res.success) this.budgets = res.data;
      }
    });

    // Load recent expenses (all, then take last 5)
    this.expenseService.getExpensesByMonth(this.currentMonth).subscribe({
      next: (res) => {
        if (res.success) {
          // Show only the 5 most recent expenses
          this.recentExpenses = res.data.slice(-5).reverse();
        }
      }
    });

    // Load unread alerts count
    this.alertService.getUnreadAlerts().subscribe({
      next: (res) => {
        if (res.success) this.unreadAlertsCount = res.data.length;
        this.isLoading = false;
      }
    });

    // Load ML prediction for next month
    this.http.get<ApiResponse<{ predicted_amount: number; trend: string }>>(
      `${environment.apiUrl}/api/ml/predict`
    ).subscribe({
      next: (res) => {
        if (res.success) this.prediction = res.data;
      }
    });
  }

  /**
   * Get the CSS class for a budget progress bar based on percentage used.
   * Green < 50%, Orange 50-80%, Red > 80%.
   *
   * @param percentage the budget usage percentage
   * @returns CSS class name for the progress bar container
   */
  getBudgetProgressClass(percentage: number): string {
    if (percentage < 50) return 'progress-low';
    if (percentage < 80) return 'progress-medium';
    return 'progress-high';
  }

  /**
   * Get the CSS class for the grade display badge.
   *
   * @param grade the letter grade ('A', 'B', or 'C')
   * @returns CSS class name for the grade badge
   */
  getGradeClass(grade: string): string {
    return `grade-${grade.toLowerCase()}`;
  }
}
