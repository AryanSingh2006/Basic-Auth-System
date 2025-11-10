package com.example.Basic_Auth_System.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Basic_Auth_System.Dto.LoginRequest;
import com.example.Basic_Auth_System.Dto.RegisterRequest;
import com.example.Basic_Auth_System.Exception.DuplicateUsernameException;
import com.example.Basic_Auth_System.Exception.InvalidCredentialsException;
import com.example.Basic_Auth_System.Exception.ValidationException;
import com.example.Basic_Auth_System.Model.User;
import com.example.Basic_Auth_System.Repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  // Registration logic
  public User registerUser(RegisterRequest request) {
    // Validate input
    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new ValidationException("Username is required");
    }
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new ValidationException("Password is required");
    }
    if (request.getRole() == null || request.getRole().trim().isEmpty()) {
      throw new ValidationException("Role is required");
    }

    // Check if username already exists
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new DuplicateUsernameException("Username already exists");
    }

    // Create new user with hashed password
    User newUser = new User(
        request.getUsername(),
        passwordEncoder.encode(request.getPassword()),
        request.getRole());

    // Save user to the Database
      userRepository.save(newUser);
  }

  public String verify(LoginRequest request) {
    // Validate input
    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new ValidationException("Username is required");
    }
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new ValidationException("Password is required");
    }

    try {
      // Authenticate user credentials
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      // Get the authenticated user from repository
      User user = userRepository.findByUsername(request.getUsername());

      // Generate and return JWT token
      return jwtService.generateToken(user);
    } catch (BadCredentialsException e) {
      throw new InvalidCredentialsException("Invalid username or password");
    }
  }

  public void logout(HttpServletRequest request) {
    // Extract Authorization header
    String header = request.getHeader("Authorization");

    // Check the header exist and has Bearer as prefix
    if (header == null || !header.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Missing or invalid Authorization header");
    }

    // Extract token from the header
    String token = header.substring(7).trim();

    // blacklist the token using JwtService
    jwtService.blacklistToken(token);
  }
}
