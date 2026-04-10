package Um6p.Project.DigitalCulture.SpendSmart.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Main Spring Security configuration class.
 * Configures JWT-based stateless authentication, route protection,
 * CORS for Angular (port 4200) and Android, and BCrypt password encoding.
 * All /api/auth/** routes are public; all other /api/** routes require a valid JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** Custom JWT authentication filter — runs before the default auth filter */
    private final JwtAuthFilter jwtAuthFilter;

    /** Custom service to load user details from the database */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Configures the main security filter chain for HTTP requests.
     * Sets up stateless sessions, disables CSRF (not needed for REST APIs),
     * defines public and protected routes, and registers the JWT filter.
     *
     * @param httpSecurity the Spring Security HttpSecurity builder
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Disable CSRF — not needed for stateless JWT-based REST APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Enable CORS with our custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Define which routes are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to registration and login endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // Category creation requires ADMIN role
                        .requestMatchers("POST /api/categories").hasRole("ADMIN")
                        // All other API routes require a valid JWT token
                        .anyRequest().authenticated()
                )
                // Use stateless session — no HTTP sessions, authentication via JWT only
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Register our custom authentication provider
                .authenticationProvider(authenticationProvider())
                // Add our JWT filter before Spring's default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) to allow requests from Angular
     * (port 4200) and Android apps. This is required for the frontend to communicate
     * with the Spring Boot backend.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from Angular (port 4200) and any Android app
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Allow standard HTTP methods used by our API
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow the Authorization header (for JWT) and Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Allow credentials (cookies, authorization headers) in CORS requests
        configuration.setAllowCredentials(true);

        // Apply this CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Creates a DaoAuthenticationProvider that uses our custom UserDetailsService
     * and BCrypt password encoder to authenticate users.
     *
     * @return the configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Use our custom service to load users from the database
        provider.setUserDetailsService(userDetailsService);
        // Use BCrypt to verify passwords
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the AuthenticationManager bean used to perform authentication
     * in the AuthRestController during login.
     *
     * @param authenticationConfiguration Spring's auth configuration
     * @return the AuthenticationManager
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates a BCryptPasswordEncoder bean for encrypting and verifying passwords.
     * BCrypt automatically handles salting, making passwords resistant to rainbow table attacks.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
