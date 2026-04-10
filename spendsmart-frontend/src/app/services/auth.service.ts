import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { User } from '../models/user.model';

/**
 * Authentication service handling login, registration, logout, and JWT token management.
 * Tokens are stored in localStorage under the 'spendsmart_token' key.
 * The user's email is stored under 'spendsmart_email'.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  /** Base URL for the authentication endpoints */
  private readonly authUrl = `${environment.apiUrl}/api/auth`;

  /** LocalStorage key for the JWT token */
  private readonly TOKEN_KEY = 'spendsmart_token';

  /** LocalStorage key for the user's email */
  private readonly EMAIL_KEY = 'spendsmart_email';

  constructor(private http: HttpClient) {}

  /**
   * Authenticate the user with email and password.
   * On success, saves the JWT token and email to localStorage.
   *
   * @param email    the user's email address
   * @param password the user's plain-text password
   * @returns an Observable of the API response containing the JWT token
   */
  login(email: string, password: string): Observable<ApiResponse<{ token: string; email: string }>> {
    return this.http.post<ApiResponse<{ token: string; email: string }>>(
      `${this.authUrl}/login`,
      { email, password }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          // Store the JWT token and email in localStorage for future requests
          localStorage.setItem(this.TOKEN_KEY, response.data.token);
          localStorage.setItem(this.EMAIL_KEY, response.data.email);
        }
      })
    );
  }

  /**
   * Register a new user account.
   *
   * @param user the user registration data (firstName, lastName, email, password)
   * @returns an Observable of the API response containing the created user
   */
  register(user: User): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.authUrl}/register`, user);
  }

  /**
   * Log out the current user by removing the JWT token and email from localStorage.
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.EMAIL_KEY);
  }

  /**
   * Check if a user is currently logged in by verifying the JWT token exists.
   *
   * @returns true if a JWT token is present in localStorage, false otherwise
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    // Verify the token is not expired by checking the expiration claim
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationTime = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < expirationTime;
    } catch {
      return false;
    }
  }

  /**
   * Retrieve the JWT token from localStorage.
   *
   * @returns the JWT token string, or null if not present
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Get the currently logged-in user's email address.
   * First tries localStorage, then falls back to decoding the JWT payload.
   *
   * @returns the user's email address, or null if not logged in
   */
  getCurrentUserEmail(): string | null {
    // Try to get from localStorage first (faster)
    const email = localStorage.getItem(this.EMAIL_KEY);
    if (email) return email;

    // Fall back to decoding the JWT token payload
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || null;
    } catch {
      return null;
    }
  }
}
