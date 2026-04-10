package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User registration and profile data.
 * Used to receive user data from the API without exposing the entity directly.
 * Password field is only used during registration — never returned in responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    /** User's unique identifier (only present in responses, not required in requests) */
    private Long id;

    /** User's first name — must not be blank */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** User's last name — must not be blank */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /** User's email address — must be a valid email format */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /** Plain-text password — only used during registration, minimum 6 characters */
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** User's role: "USER" or "ADMIN" */
    private String role;
}
