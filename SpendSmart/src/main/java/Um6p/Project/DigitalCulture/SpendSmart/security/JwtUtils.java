package Um6p.Project.DigitalCulture.SpendSmart.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating, parsing, and validating JWT (JSON Web Token) tokens.
 * Tokens are signed with an HMAC-SHA256 key read from application.properties.
 * Each token contains the user's email as the subject and an expiration timestamp.
 */
@Component
@Slf4j
public class JwtUtils {

    /** Secret key used for signing JWT tokens — loaded from application.properties */
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /** JWT expiration time in milliseconds — loaded from application.properties */
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generate a JWT token for a given user with no extra claims.
     *
     * @param userDetails the authenticated user's details
     * @return a signed JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a JWT token with custom claims.
     *
     * @param extraClaims additional key-value pairs to include in the token payload
     * @param userDetails the authenticated user's details
     * @return a signed JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                // Subject is the user's email address
                .setSubject(userDetails.getUsername())
                // Token issued at the current time
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Token expires after the configured duration
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                // Sign with HMAC-SHA256 using the secret key
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the email (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return the email address stored in the token's subject claim
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration date from a JWT token.
     *
     * @param token the JWT token string
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          the JWT token string
     * @param claimsResolver a function that extracts the desired claim from the Claims object
     * @param <T>            the type of the claim value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validate a JWT token against the given user details.
     * Checks that the token's subject matches the user's email and that it is not expired.
     *
     * @param token       the JWT token string to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Check whether a JWT token has expired.
     *
     * @param token the JWT token string
     * @return true if the token is expired, false if still valid
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Parse and extract all claims from a JWT token.
     * Throws a JwtException if the token is invalid or tampered.
     *
     * @param token the JWT token string
     * @return all Claims contained in the token payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decode the secret key from hex and create the HMAC-SHA signing key.
     *
     * @return a Key object used to sign and verify JWT tokens
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
