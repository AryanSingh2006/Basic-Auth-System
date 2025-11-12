package com.example.Basic_Auth_System.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Basic_Auth_System.Repository.UserRepository;
import com.example.Basic_Auth_System.Model.UserPrincipal;
import com.example.Basic_Auth_System.Model.User;

//We are implementing this UserDetailsService Interface and makes a class CustomUserDetailsService in which
//we will override the method that was inside the UserDetailsService and Find the user by there Username
//We have defined the loadUserByUsername in the UserRepository
//Later we are returning the user warp inside the UserPrincipal
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Loads user details from the database by username.
    // Returns a UserDetails object that Spring Security can use for authentication.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("user not found");
        }

        return new UserPrincipal(user);
    }
}
