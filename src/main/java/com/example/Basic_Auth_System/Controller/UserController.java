package com.example.Basic_Auth_System.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Basic_Auth_System.Model.User;

@RestController
@RequestMapping("/api")
public class UserController {

  //Protected endpoint (/api/me)
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser() {
    // Get current authenticated user from Spring Security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if user is authenticated
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body("User not authenticated");
    }

    // Extract User object from authentication
    User user = (User) authentication.getPrincipal();

    //Return user
    return ResponseEntity.ok(user);
  }

  //Admin only endpoint (/api/admin/users)
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/users")
  public ResponseEntity<?> getAdminDashboard() {
    // Get current authenticated user from Spring Security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Extract User object
    User user = (User) authentication.getPrincipal();

    //Return Admin response
    return ResponseEntity.ok("Welcome Admin " + user.getUsername() + "! This is admin dashboard.");
  }

}
