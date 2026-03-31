package com.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Tiện ích generate và validate JWT token.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT từ Authentication object sau khi login thành công.
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userDetails.getEmail())
                .claim("userId", userDetails.getId())
                .claim("role", userDetails.getRole())
                .claim("donViId", userDetails.getDonViId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Lấy email từ JWT token.
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Lấy userId embed trong JWT.
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * Lấy role embed trong JWT.
     */
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Lấy donViId embed trong JWT.
     */
    public Long getDonViIdFromToken(String token) {
        return parseClaims(token).get("donViId", Long.class);
    }

    /**
     * Validate token — trả false nếu expired, malformed, invalid signature, v.v.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token không được hỗ trợ: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token không hợp lệ: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature không hợp lệ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims rỗng: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
