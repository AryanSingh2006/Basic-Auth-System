package com.example.Basic_Auth_System.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Basic_Auth_System.Model.User;
import com.example.Basic_Auth_System.Model.UserPrincipal;
import com.example.Basic_Auth_System.Repository.UserRepository;
import com.example.Basic_Auth_System.Service.JwtService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    //Extracting token from the header(Authorization: Bearer <token> header)
    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      // No token provided, so skip this filter and continue to the next filter in the chain
      filterChain.doFilter(request, response); 
      return;
    }

    //Extract token by removing the Bearer
    String token = header.substring(7).trim();

    try {
      //Exctracting username from the token
      String username = jwtService.extractUsername(token);

      //Checking if there is a token
      if (username == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        return;
      }

      //Checking if the token is valid
      if (!jwtService.validateToken(token)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired or invalid");
        return;
      }

      // Check if token is blacklisted (user has logged out)
      if (jwtService.isTokenBlacklisted(token)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
        return;
      }

      //Only authenticate users who are not already authenticated
      if (SecurityContextHolder.getContext().getAuthentication() == null) {

        //fetch the user from the database using username obtained from the JWT
        User user = userRepository.findByUsername(username);

        if (user != null) {
          //Implements UserPrinciple
          UserPrincipal principal = new UserPrincipal(user);

          //Creating authentication token with user and authorities
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
              principal.getAuthorities());

          //Set authentication in Spring Security context
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
          return;
        }
      } 

    } catch (Exception ex) {
      System.out.println("JWT authentication failed: " + ex.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
      return;
    }

    filterChain.doFilter(request, response);
  }

}
