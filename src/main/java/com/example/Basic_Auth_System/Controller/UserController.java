package com.example.Basic_Auth_System.Controller;

import com.example.Basic_Auth_System.Dto.UserResponse;
import com.example.Basic_Auth_System.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private UserService userService;

    //Protected endpoint (/api/me)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        return ResponseEntity.ok(user);
    }

    //Admin only endpoint (/api/admin/users)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAdminDashboard() {
        UserResponse user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("not auth");
        }
        return ResponseEntity.ok(user);
    }

}
