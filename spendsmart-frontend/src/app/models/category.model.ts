/**
 * Model representing a spending category (e.g. Food, Transport, Health).
 * Used in expense forms, budget forms, and category dropdowns.
 */
export interface Category {
  /** Unique category identifier */
  id: number;
  /** Display name of the category */
  name: string;
  /** Hex color code for visual representation (e.g. '#FF6B6B') */
  color: string;
  /** Icon name for UI representation (e.g. 'restaurant') */
  icon?: string;
}
