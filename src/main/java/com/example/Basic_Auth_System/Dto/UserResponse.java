package com.example.Basic_Auth_System.Dto;

public class UserResponse {
    private String username;
    private String role;

    public UserResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRole() {
        return this.role;
    }
}
