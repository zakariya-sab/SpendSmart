/**
 * Model representing a monthly spending budget for a category.
 * Includes calculated fields (remainingAmount, percentageUsed) returned by the API.
 */
export interface Budget {
  /** Unique budget identifier */
  id?: number;
  /** Maximum spending limit for this budget period */
  maxAmount: number;
  /** Amount spent so far in this budget period */
  spentAmount?: number;
  /** Remaining amount (maxAmount - spentAmount) */
  remainingAmount?: number;
  /** Percentage of budget used (0-100), used for progress bars */
  percentageUsed?: number;
  /** The month this budget applies to in 'YYYY-MM' format */
  month: string;
  /** ID of the category this budget is allocated to */
  categoryId: number;
  /** Category display name — returned in API responses */
  categoryName?: string;
  /** Category hex color code — used for progress bar color coding */
  categoryColor?: string;
}
