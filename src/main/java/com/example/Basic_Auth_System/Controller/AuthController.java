package com.example.Basic_Auth_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Basic_Auth_System.Dto.LoginRequest;
import com.example.Basic_Auth_System.Dto.LoginResponse;
import com.example.Basic_Auth_System.Dto.RegisterRequest;
import com.example.Basic_Auth_System.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  //Register Endpoint
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    userService.registerUser(request);
    Map<String, String> response = new HashMap<>();
    response.put("message", "User registered successfully");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  //Login Endpoint
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    String token = userService.verify(request);
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    return ResponseEntity.ok(response);
  }

  //Logout Endpoint
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    userService.logout(request);
    Map<String, String> response = new HashMap<>();
    response.put("message", "Logged out successfully");
    return ResponseEntity.ok(response);
  }
}
