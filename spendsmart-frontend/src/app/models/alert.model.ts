/**
 * Model representing a financial alert triggered by budget threshold crossing.
 * Alerts are read-only from the client's perspective — created automatically by the server.
 */
export interface Alert {
  /** Unique alert identifier */
  id: number;
  /** Human-readable description of the alert */
  message: string;
  /** Date and time when the alert was created */
  date: Date | string;
  /** Whether the user has acknowledged this alert */
  isRead: boolean;
  /** Alert type: 'WARNING' (80%+ usage) or 'EXCEEDED' (100%+ usage) */
  type: 'WARNING' | 'EXCEEDED';
  /** The category name that triggered this alert */
  categoryName?: string;
  /** The budget month in 'YYYY-MM' format */
  budgetMonth?: string;
}
