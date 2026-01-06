package com.hms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for creating and validating JWT tokens.
 * Handles token generation, parsing, and verification for user authentication.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;  // Secret key from application.properties

    @Value("${jwt.expiration}")
    private long expiration;  // Token expiration time in milliseconds

    /**
     * Creates the signing key from the secret for token encryption.
     */
    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (email) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token using a custom function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims (data) stored in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Use secret key to verify
                .build()
                .parseClaimsJws(token)  // Parse and verify token
                .getBody();  // Get the claims
    }

    /**
     * Checks if the token has expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a new JWT token for a user.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates the actual JWT token with claims, subject, and expiration.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)  // Add custom data (currently empty)
                .setSubject(subject)  // Set username (email)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // Token expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign with secret key
                .compact();  // Build the token string
    }

    /**
     * Validates if the token is valid for the given user and not expired.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Check username matches and token hasn't expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}