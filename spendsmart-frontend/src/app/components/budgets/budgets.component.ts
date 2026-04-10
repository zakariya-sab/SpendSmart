import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService } from '../../services/budget.service';
import { CategoryService } from '../../services/category.service';
import { Budget } from '../../models/budget.model';
import { Category } from '../../models/category.model';

/**
 * Budgets component — displays and manages monthly spending budgets.
 * Features:
 *   - List of budgets for the current month with colored progress bars
 *   - Color coding: green < 50%, orange 50-80%, red > 80% usage
 *   - Add new budget form with category and max amount selection
 *   - Month selector to view budgets from other months
 */
@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './budgets.component.html'
})
export class BudgetsComponent implements OnInit {

  /** Budgets for the selected month */
  budgets: Budget[] = [];

  /** All available categories for the dropdown */
  categories: Category[] = [];

  /** The month being viewed in 'YYYY-MM' format */
  selectedMonth: string;

  /** Whether the add budget form is visible */
  showAddForm = false;

  /** Whether data is loading */
  isLoading = false;

  /** Feedback message to display */
  message = '';

  /** Whether the last operation was successful */
  isSuccess = true;

  /** Form model for creating a new budget */
  newBudget: Budget = {
    maxAmount: 0,
    month: '',
    categoryId: 0
  };

  constructor(
    private budgetService: BudgetService,
    private categoryService: CategoryService
  ) {
    const now = new Date();
    this.selectedMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    this.newBudget.month = this.selectedMonth;
  }

  /** Load budgets and categories when the component initializes */
  ngOnInit(): void {
    this.loadBudgets();
    this.loadCategories();
  }

  /**
   * Load budgets for the selected month.
   */
  loadBudgets(): void {
    this.isLoading = true;
    this.budgetService.getBudgetsByMonth(this.selectedMonth).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) this.budgets = res.data;
      },
      error: () => { this.isLoading = false; }
    });
  }

  /** Load available categories for the dropdown selector */
  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        if (res.success) this.categories = res.data;
      }
    });
  }

  /**
   * Submit the add budget form.
   * Creates a new budget for the selected category and month.
   */
  createBudget(): void {
    if (!this.newBudget.maxAmount || !this.newBudget.categoryId) {
      this.showMessage('Please fill in all required fields.', false);
      return;
    }

    this.budgetService.createBudget(this.newBudget).subscribe({
      next: (res) => {
        if (res.success) {
          this.showMessage('Budget created successfully!', true);
          this.showAddForm = false;
          this.resetForm();
          this.loadBudgets();
        } else {
          this.showMessage(res.message, false);
        }
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Failed to create budget.', false);
      }
    });
  }

  /**
   * Get CSS class for budget progress bar color based on usage percentage.
   * Green < 50%, Orange 50-80%, Red > 80%.
   *
   * @param percentage the budget usage percentage
   * @returns CSS class name
   */
  getProgressClass(percentage: number): string {
    if (percentage < 50) return 'progress-low';
    if (percentage < 80) return 'progress-medium';
    return 'progress-high';
  }

  /**
   * Reset the add budget form to its default state.
   */
  resetForm(): void {
    this.newBudget = { maxAmount: 0, month: this.selectedMonth, categoryId: 0 };
  }

  /**
   * Display a feedback message and auto-hide it after 3 seconds.
   *
   * @param msg       the message to display
   * @param isSuccess whether this is a success or error message
   */
  showMessage(msg: string, isSuccess: boolean): void {
    this.message = msg;
    this.isSuccess = isSuccess;
    setTimeout(() => this.message = '', 3000);
  }
}
