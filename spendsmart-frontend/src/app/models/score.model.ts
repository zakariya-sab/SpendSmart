/**
 * Model representing a monthly financial health score.
 * Used on the dashboard and in the statistics chart.
 */
export interface Score {
  /** Unique score identifier */
  id: number;
  /** Numerical score value from 0 to 100 */
  value: number;
  /** Letter grade: 'A' (excellent), 'B' (good), 'C' (needs improvement) */
  grade: 'A' | 'B' | 'C';
  /** The month this score applies to in 'YYYY-MM' format */
  month: string;
  /** Date and time when the score was calculated */
  date: Date | string;
}
