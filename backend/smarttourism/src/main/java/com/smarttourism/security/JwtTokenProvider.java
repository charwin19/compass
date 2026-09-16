package com.smarttourism.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT utility — generates and validates JSON Web Tokens.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final Key signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        // HMAC-SHA key derived from the configured secret
        this.signingKey  = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a signed JWT for the authenticated user.
     *
     * @param userId   user's database ID (subject)
     * @param email    stored as a custom claim
     * @param role     stored as a custom claim
     */
    public String generateToken(Long userId, String email, String role) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role",  role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extract the user ID (subject) from a token. */
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /** Extract the email claim from a token. */
    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /** Extract the role claim from a token. */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /** Validate a JWT — returns true if it is properly signed and not expired. */
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    // ---- private helpers ----

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
