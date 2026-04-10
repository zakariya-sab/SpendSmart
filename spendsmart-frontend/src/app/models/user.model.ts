/**
 * Model representing a user account in the SpendSmart application.
 * Used for registration, profile display, and JWT payload decoding.
 */
export interface User {
  /** Unique user identifier */
  id?: number;
  /** User's first name */
  firstName: string;
  /** User's last name */
  lastName: string;
  /** Email address used as login identifier */
  email: string;
  /** Password — only present in registration requests, never in responses */
  password?: string;
  /** User role: 'USER' or 'ADMIN' */
  role?: string;
}
