package com.example.Basic_Auth_System.Service;

import com.example.Basic_Auth_System.Model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // Registration logic
    public void registerUser(RegisterRequest request) {
        // Validate input(If an of the input field is null it will throw an exception
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
                passwordEncoder.encode(request.getPassword()), //Hashed the password using the .encode()
                request.getRole());


        // Save user to the Database
        userRepository.save(newUser);
    }

    public String verify(LoginRequest request) {
        // Validate input(If any of the input field is null or empty this will throw an exception)
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }

        try {
            // Authenticate user credentials
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Get the authenticated user from repository
            UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();

            // Generate and return JWT token
            return jwtService.generateToken(userPrincipal.getUser());
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
