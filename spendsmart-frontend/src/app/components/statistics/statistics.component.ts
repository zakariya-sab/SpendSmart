import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { ScoreService } from '../../services/score.service';
import { Score } from '../../models/score.model';
import { Chart, registerables } from 'chart.js';

// Register all Chart.js components (required in Chart.js v3+)
Chart.register(...registerables);

/**
 * Statistics component — data visualization for financial analysis.
 * Displays three charts using Chart.js:
 *   1. Pie chart: spending by category for the current month
 *   2. Bar chart: monthly spending evolution for the last 6 months
 *   3. Line chart: financial health score evolution over time
 */
@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.component.html'
})
export class StatisticsComponent implements OnInit, AfterViewInit {

  /** Canvas element reference for the category pie chart */
  @ViewChild('categoryChart') categoryChartRef!: ElementRef<HTMLCanvasElement>;

  /** Canvas element reference for the monthly spending bar chart */
  @ViewChild('monthlyChart') monthlyChartRef!: ElementRef<HTMLCanvasElement>;

  /** Canvas element reference for the score evolution line chart */
  @ViewChild('scoreChart') scoreChartRef!: ElementRef<HTMLCanvasElement>;

  /** Financial health scores history */
  scores: Score[] = [];

  /** Whether data is loading */
  isLoading = true;

  /** Chart.js instances for cleanup on destroy */
  private charts: Chart[] = [];

  /** Current month in 'YYYY-MM' format */
  currentMonth: string;

  constructor(
    private expenseService: ExpenseService,
    private scoreService: ScoreService
  ) {
    const now = new Date();
    this.currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  }

  ngOnInit(): void {
    this.loadScores();
  }

  /**
   * Initialize charts after the view is rendered (canvas elements must be in the DOM).
   */
  ngAfterViewInit(): void {
    this.loadAndBuildCharts();
  }

  /**
   * Load health scores from the API.
   */
  loadScores(): void {
    this.scoreService.getAllScores().subscribe({
      next: (res) => {
        if (res.success) this.scores = res.data;
      }
    });
  }

  /**
   * Load expense data and build all three charts.
   * Called after the view is initialized so canvas elements exist.
   */
  loadAndBuildCharts(): void {
    // Load current month expenses for the pie chart
    this.expenseService.getExpensesByMonth(this.currentMonth).subscribe({
      next: (res) => {
        if (res.success) {
          this.buildCategoryChart(res.data);
        }
      }
    });

    // Load last 6 months for the bar chart
    this.buildMonthlyChart();

    // Build score chart with pre-loaded scores
    setTimeout(() => this.buildScoreChart(), 500);
    this.isLoading = false;
  }

  /**
   * Build the pie chart showing spending by category for the current month.
   *
   * @param expenses the list of expenses to categorize
   */
  buildCategoryChart(expenses: any[]): void {
    // Group expenses by category and sum amounts
    const categoryTotals: { [key: string]: { total: number; color: string } } = {};
    expenses.forEach(exp => {
      if (!categoryTotals[exp.categoryName]) {
        categoryTotals[exp.categoryName] = { total: 0, color: exp.categoryColor };
      }
      categoryTotals[exp.categoryName].total += exp.amount;
    });

    const labels = Object.keys(categoryTotals);
    const data = labels.map(l => categoryTotals[l].total);
    const colors = labels.map(l => categoryTotals[l].color);

    if (!this.categoryChartRef) return;

    const chart = new Chart(this.categoryChartRef.nativeElement, {
      type: 'pie',
      data: {
        labels,
        datasets: [{ data, backgroundColor: colors, borderWidth: 2, borderColor: '#fff' }]
      },
      options: {
        responsive: true,
        plugins: { legend: { position: 'bottom' }, title: { display: false } }
      }
    });
    this.charts.push(chart);
  }

  /**
   * Build the bar chart showing total spending for the last 6 months.
   */
  buildMonthlyChart(): void {
    const labels: string[] = [];
    const data: number[] = [];
    const promises: Promise<number>[] = [];

    // Build promises for each of the last 6 months
    for (let i = 5; i >= 0; i--) {
      const date = new Date();
      date.setMonth(date.getMonth() - i);
      const month = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      labels.push(month);

      const promise = new Promise<number>((resolve) => {
        this.expenseService.getExpensesByMonth(month).subscribe({
          next: (res) => {
            const total = res.success ? res.data.reduce((sum, e) => sum + e.amount, 0) : 0;
            resolve(total);
          },
          error: () => resolve(0)
        });
      });
      promises.push(promise);
    }

    // Build chart after all months are loaded
    Promise.all(promises).then(totals => {
      if (!this.monthlyChartRef) return;
      const chart = new Chart(this.monthlyChartRef.nativeElement, {
        type: 'bar',
        data: {
          labels,
          datasets: [{
            label: 'Total Spending (MAD)',
            data: totals,
            backgroundColor: '#F97316',
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          plugins: { legend: { display: false } },
          scales: { y: { beginAtZero: true } }
        }
      });
      this.charts.push(chart);
    });
  }

  /**
   * Build the line chart showing financial health score evolution.
   */
  buildScoreChart(): void {
    this.scoreService.getAllScores().subscribe({
      next: (res) => {
        if (!res.success || !this.scoreChartRef) return;

        const scores = res.data.reverse(); // Show oldest first
        const labels = scores.map(s => s.month);
        const values = scores.map(s => s.value);
        const colors = scores.map(s =>
          s.grade === 'A' ? '#22C55E' : s.grade === 'B' ? '#F59E0B' : '#EF4444'
        );

        const chart = new Chart(this.scoreChartRef.nativeElement, {
          type: 'line',
          data: {
            labels,
            datasets: [{
              label: 'Health Score',
              data: values,
              borderColor: '#F97316',
              backgroundColor: 'rgba(249, 115, 22, 0.1)',
              tension: 0.4,
              fill: true,
              pointBackgroundColor: colors
            }]
          },
          options: {
            responsive: true,
            scales: { y: { min: 0, max: 100 } }
          }
        });
        this.charts.push(chart);
      }
    });
  }
}
