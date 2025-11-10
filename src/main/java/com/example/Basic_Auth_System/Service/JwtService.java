package com.example.Basic_Auth_System.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.Basic_Auth_System.Model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  //jwt secret stored in the application.properties
  @Value("${jwt.secret}")
  private String jwtsecretKey;

  // HashSet to store blacklisted tokens (tokens that have been logged out)
  private final Set<String> blacklistedTokens = new HashSet<>();

  //Generates a Secret Key with the help of HMAC
  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtsecretKey.getBytes(StandardCharsets.UTF_8));
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
      System.out.println("Error extracting username: " + e.getMessage());
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
      System.out.println("Token has expired: " + e.getMessage());
      return false;
    } catch (JwtException e) {
      System.out.println("Invalid JWT token: " + e.getMessage());
      return false;
    } catch (Exception e) {
      System.out.println("Token validation error: " + e.getMessage());
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
