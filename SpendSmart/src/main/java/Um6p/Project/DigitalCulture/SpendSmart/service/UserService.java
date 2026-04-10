package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.UserDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service class for user registration and account management.
 * Handles new user creation with encrypted passwords and role assignment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    /** Repository for user database operations */
    private final UserRepository userRepository;

    /** Password encoder for BCrypt hashing */
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user account.
     * Validates that the email is not already taken, encrypts the password,
     * and saves the new user with the default "USER" role.
     *
     * @param dto the user registration data (firstName, lastName, email, password)
     * @return the saved user as a UserDTO (without the password)
     * @throws RuntimeException if a user with this email already exists
     */
    public UserDTO registerUser(UserDTO dto) {
        // Check if email is already registered
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("An account with email '" + dto.getEmail() + "' already exists");
        }

        // Build the new user entity with BCrypt-encrypted password
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                // Encrypt the plain-text password before storing it
                .password(passwordEncoder.encode(dto.getPassword()))
                // New users get the default "USER" role
                .role("USER")
                .createdAt(new Date())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user: {}", savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    /**
     * Convert a User entity to a UserDTO for API responses.
     * The password field is NOT included in the response for security.
     *
     * @param user the User entity to convert
     * @return a UserDTO with all fields except password
     */
    public UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                // Never include the password in API responses
                .password(null)
                .role(user.getRole())
                .build();
    }
}
