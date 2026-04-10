import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

/**
 * Registration component for creating a new SpendSmart account.
 * Calls POST /api/auth/register and redirects to /login on success.
 * Shows validation errors and server error messages.
 */
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  /** User's first name */
  firstName = '';

  /** User's last name */
  lastName = '';

  /** User's email address */
  email = '';

  /** User's password (minimum 6 characters) */
  password = '';

  /** Error message to display when registration fails */
  errorMessage = '';

  /** Success message to display after successful registration */
  successMessage = '';

  /** Whether a registration request is in progress */
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Handle the registration form submission.
   * Creates the user account and navigates to the login page on success.
   */
  onRegister(): void {
    if (!this.firstName || !this.lastName || !this.email || !this.password) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    if (this.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register({
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = 'Account created! Redirecting to login...';
          // Redirect to login after 1.5 seconds
          setTimeout(() => this.router.navigate(['/login']), 1500);
        } else {
          this.errorMessage = response.message;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
