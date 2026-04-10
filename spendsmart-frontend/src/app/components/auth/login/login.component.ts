import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

/**
 * Login component that displays the authentication form.
 * Calls POST /api/auth/login, saves the JWT token, and redirects to the dashboard.
 * Shows validation errors and server error messages to the user.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  /** Email address entered by the user */
  email = '';

  /** Password entered by the user */
  password = '';

  /** Error message to display when login fails */
  errorMessage = '';

  /** Whether a login request is in progress (disables the submit button) */
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Handle the login form submission.
   * Authenticates the user, saves the token, and navigates to the dashboard.
   */
  onLogin(): void {
    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter your email and password.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          // Redirect to dashboard after successful login
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = response.message;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Login failed. Please check your credentials.';
      }
    });
  }
}
