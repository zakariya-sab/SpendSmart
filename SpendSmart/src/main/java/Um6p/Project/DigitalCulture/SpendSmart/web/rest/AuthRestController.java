package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.LoginRequestDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.UserDTO;
import Um6p.Project.DigitalCulture.SpendSmart.security.JwtUtils;
import Um6p.Project.DigitalCulture.SpendSmart.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles user registration and login endpoints.
 * These endpoints are public (no JWT token required).
 * Base path: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {

    /** Service for user registration logic */
    private final UserService userService;

    /** Spring Security authentication manager for validating credentials */
    private final AuthenticationManager authenticationManager;

    /** JWT utility for generating tokens after successful login */
    private final JwtUtils jwtUtils;

    /**
     * Register a new user account.
     * Validates the request body, creates the user with encrypted password,
     * and returns the created user data.
     *
     * POST /api/auth/register
     *
     * @param userDTO the registration data (firstName, lastName, email, password)
     * @return 200 with the created user, or 400 if email is already taken
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.registerUser(userDTO);
            return ResponseEntity.ok(ApiResponseDTO.success("User registered successfully", createdUser));
        } catch (RuntimeException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Authenticate a user and return a JWT token.
     * Validates email and password, then generates a JWT token for the session.
     * The client should include this token in the Authorization header of all future requests.
     *
     * POST /api/auth/login
     *
     * @param loginRequest the login credentials (email and password)
     * @return 200 with JWT token and user email, or 401 if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        try {
            // Authenticate the user via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Generate a JWT token for the authenticated user
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateToken(userDetails);

            // Return the token and email in the response
            Map<String, String> tokenData = new HashMap<>();
            tokenData.put("token", jwtToken);
            tokenData.put("email", userDetails.getUsername());

            log.info("User logged in: {}", userDetails.getUsername());
            return ResponseEntity.ok(ApiResponseDTO.success("Login successful", tokenData));

        } catch (BadCredentialsException e) {
            log.warn("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid email or password"));
        }
    }
}
