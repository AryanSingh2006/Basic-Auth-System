package com.example.Basic_Auth_System.Service;

import com.example.Basic_Auth_System.Model.UserPrincipal;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    // Registration logic
    public void registerUser(RegisterRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String role = request.getRole();

        // Validate input(If an of the input field is null it will throw an exception
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Role is required");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);
        // Create new user with hashed password
        User newUser = new User(
                username,
                encodedPassword,
                role);

        userRepository.save(newUser);
    }

    public String verify(LoginRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();

        // Validate input(If any of the input field is null or empty this will throw an exception)
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }

        try {
            // Authenticate user credentials
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

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
            throw new ValidationException("Missing or invalid Authorization header");
        }

        // Extract token from the header
        String token = header.substring(7).trim();

        // blacklist the token using JwtService
        jwtService.blacklistToken(token);
    }
}
