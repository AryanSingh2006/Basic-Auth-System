package com.example.Basic_Auth_System.Service;

import com.example.Basic_Auth_System.Dto.UserResponse;
import com.example.Basic_Auth_System.Model.User;
import com.example.Basic_Auth_System.Model.UserPrincipal;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        return new UserResponse(user.getUsername(), user.getRole());
    }
}
