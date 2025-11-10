package com.example.Basic_Auth_System.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; 

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String role; // USER / ADMIN

  // No Argument Constructor
  public User() {
  }

  // Parameterized Constructor, new User("Jeo", "hash", "USER");
  public User(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  // Getters and Setters

  public Long getId() {
    return id;
  }

  public Long setId(Long id) {
    return this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public String setUsername(String username) {
    return this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public String setPassword(String password) {
    return this.password = password;
  }

  public String getRole() {
    return role;
  }

  public String setRole(String role) {
    return this.role = role;
  }
}
