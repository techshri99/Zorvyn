package com.finance.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT operations.
 *
 * HOW JWT WORKS (simplified):
 * 1. User logs in with email + password
 * 2. Server validates credentials and creates a signed token (JWT)
 * 3. Client sends this token in every request header: Authorization: Bearer <token>
 * 4. Server validates the token on each request to identify the user
 *
 * A JWT has 3 parts separated by dots: header.payload.signature
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret; // Read from application.properties

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // 86400000 ms = 24 hours

    /**
     * Convert the secret string into a cryptographic signing key.
     * HMAC-SHA256 algorithm is used for signing.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate a JWT token for an authenticated user.
     * The token contains the user's email as the "subject" (identifier).
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())       // email stored as subject
                .setIssuedAt(new Date())                     // when the token was created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with our secret key
                .compact();
    }

    /**
     * Extract the email (subject) from a JWT token.
     */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate a JWT token:
     * - Is it signed with our secret?
     * - Has it not expired?
     * - Does the email in the token match the logged-in user?
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Invalid or tampered token
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
