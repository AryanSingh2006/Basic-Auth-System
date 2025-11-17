package com.example.Basic_Auth_System.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.Basic_Auth_System.Model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
@Slf4j
public class JwtService {

    //jwt secret stored in the application.properties
    @Value("${jwt.secret}")
    private String jwtSecretKey;

    // HashSet to store blacklisted tokens (tokens that have been logged out)
    private final Set<String> blacklistedTokens = new HashSet<>();

    //Generates a Secret Key with the help of hmac
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    //Generates Token
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
                .signWith(getSecretKey())
                .compact();
    }

    //Extracts username from the token
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject(); // Returns username
        } catch (Exception e) {
            log.error("Error extracting username", e);
            return null;
        }
    }

    //Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired");
            return false;
        } catch (JwtException e) {
            log.warn("Invalid JWT token");
            return false;
        } catch (Exception e) {
            log.error("Token validation error", e);
            return false;
        }
    }

    // Add token to blacklist when user logs out
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    // Check if token is blacklisted (logged out)
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

}
