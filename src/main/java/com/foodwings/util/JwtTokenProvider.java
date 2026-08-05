package com.foodwings.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Creates and validates JSON Web Tokens used for stateless authentication.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${foodwings.jwt.secret}") String secret,
            @Value("${foodwings.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${foodwings.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(String email, List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parse(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        }catch (JwtException | IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("JWT ERROR: " + ex.getClass().getSimpleName());
            System.out.println("MESSAGE: " + ex.getMessage());
            return false;
        }
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
