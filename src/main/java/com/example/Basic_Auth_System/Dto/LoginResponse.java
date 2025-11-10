package com.example.Basic_Auth_System.Dto;

public class LoginResponse {

  private String token;

  // No-arg constructor
  public LoginResponse() {
  }

  // Constructor with all fields
  public LoginResponse(String token, String username, Long userid, String message, Long expiresIn) {
    this.token = token;
  }

  // Getters and Setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
