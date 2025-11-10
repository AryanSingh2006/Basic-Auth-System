package com.example.Basic_Auth_System.Exception;

public class DuplicateUsernameException extends RuntimeException {
  public DuplicateUsernameException(String message) {
    super(message);
  }
}
