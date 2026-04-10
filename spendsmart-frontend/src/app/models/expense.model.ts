/**
 * Model representing a single expense entry.
 * Used in expense tables, forms, and monthly filtering.
 */
export interface Expense {
  /** Unique expense identifier */
  id?: number;
  /** Amount of the expense in the user's currency */
  amount: number;
  /** Date when the expense occurred */
  date: Date | string;
  /** Optional descriptive note about the expense */
  description?: string;
  /** ID of the category this expense belongs to */
  categoryId: number;
  /** Category display name — returned in API responses */
  categoryName?: string;
  /** Category hex color code — used for color-coded display */
  categoryColor?: string;
}
