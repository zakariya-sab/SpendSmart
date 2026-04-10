import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExpenseService } from '../../services/expense.service';
import { CategoryService } from '../../services/category.service';
import { Expense } from '../../models/expense.model';
import { Category } from '../../models/category.model';

/**
 * Expenses component — displays and manages user expenses.
 * Features:
 *   - Table of all expenses with date, amount, category, and description
 *   - Add expense form with amount, date, description, and category dropdown
 *   - Delete expense with automatic budget and score recalculation
 *   - Filter expenses by month
 */
@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './expenses.component.html'
})
export class ExpensesComponent implements OnInit {

  /** All expenses displayed in the table */
  expenses: Expense[] = [];

  /** All available categories for the dropdown */
  categories: Category[] = [];

  /** Whether the add expense form is visible */
  showAddForm = false;

  /** Whether data is loading */
  isLoading = false;

  /** The month filter in 'YYYY-MM' format */
  filterMonth: string;

  /** Success/error message to display */
  message = '';

  /** Whether the last operation was successful */
  isSuccess = true;

  /** Form model for adding a new expense */
  newExpense: Expense = {
    amount: 0,
    date: new Date().toISOString().split('T')[0],
    description: '',
    categoryId: 0
  };

  constructor(
    private expenseService: ExpenseService,
    private categoryService: CategoryService
  ) {
    const now = new Date();
    this.filterMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  }

  /** Load expenses and categories when the component initializes */
  ngOnInit(): void {
    this.loadExpenses();
    this.loadCategories();
  }

  /**
   * Load expenses for the selected month filter.
   */
  loadExpenses(): void {
    this.isLoading = true;
    this.expenseService.getExpensesByMonth(this.filterMonth).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) this.expenses = res.data.reverse();
      },
      error: () => { this.isLoading = false; }
    });
  }

  /**
   * Load all available categories for the dropdown selector.
   */
  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        if (res.success) this.categories = res.data;
      }
    });
  }

  /**
   * Submit the add expense form.
   * Validates input and calls the API to create the expense.
   */
  addExpense(): void {
    if (!this.newExpense.amount || !this.newExpense.date || !this.newExpense.categoryId) {
      this.showMessage('Please fill in all required fields.', false);
      return;
    }

    this.expenseService.addExpense(this.newExpense).subscribe({
      next: (res) => {
        if (res.success) {
          this.showMessage('Expense added successfully!', true);
          this.showAddForm = false;
          this.resetForm();
          this.loadExpenses();
        } else {
          this.showMessage(res.message, false);
        }
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Failed to add expense.', false);
      }
    });
  }

  /**
   * Delete an expense by ID.
   *
   * @param id the ID of the expense to delete
   */
  deleteExpense(id: number): void {
    if (!confirm('Delete this expense?')) return;

    this.expenseService.deleteExpense(id).subscribe({
      next: (res) => {
        if (res.success) {
          this.showMessage('Expense deleted.', true);
          this.loadExpenses();
        }
      },
      error: () => this.showMessage('Failed to delete expense.', false)
    });
  }

  /**
   * Reset the add expense form to its default state.
   */
  resetForm(): void {
    this.newExpense = {
      amount: 0,
      date: new Date().toISOString().split('T')[0],
      description: '',
      categoryId: 0
    };
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
