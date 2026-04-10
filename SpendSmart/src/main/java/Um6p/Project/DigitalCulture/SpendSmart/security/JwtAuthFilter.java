package Um6p.Project.DigitalCulture.SpendSmart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts every incoming HTTP request.
 * Extracts and validates the JWT token from the Authorization header.
 * If the token is valid, sets the user's authentication in the SecurityContext.
 * This filter runs once per request (extends OncePerRequestFilter).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    /** Utility class for JWT token operations (generate, validate, extract claims) */
    private final JwtUtils jwtUtils;

    /** Service to load user details from the database by email */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Main filter logic — called once per HTTP request.
     * Extracts the JWT from the Authorization header, validates it,
     * and sets the authentication in the Spring Security context.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the chain of filters to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        // If there is no Authorization header or it doesn't start with "Bearer ", skip this filter
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract the JWT token by removing the "Bearer " prefix (7 characters)
            final String jwtToken = authorizationHeader.substring(7);

            // Extract the email from the JWT token payload
            final String userEmail = jwtUtils.extractEmail(jwtToken);

            // Only authenticate if the email was extracted and the user is not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load the user's details from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Validate the token against the user's details
                if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                    // Create the authentication token with the user's authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Attach request details (IP, session ID, etc.) to the authentication
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store the authentication in the SecurityContext for this request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log the error but do not throw — the request will proceed as unauthenticated
            log.error("JWT authentication failed: {}", e.getMessage());
        }

        // Continue processing the request through the rest of the filter chain
        filterChain.doFilter(request, response);
    }
}
