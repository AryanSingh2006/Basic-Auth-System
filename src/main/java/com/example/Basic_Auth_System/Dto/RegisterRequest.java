package com.example.Basic_Auth_System.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
    private String role;

}
