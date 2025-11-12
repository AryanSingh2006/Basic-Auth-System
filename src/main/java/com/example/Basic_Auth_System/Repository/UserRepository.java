package com.example.Basic_Auth_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Basic_Auth_System.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Find user by there username
    User findByUsername(String username);

    //check if the user exist in the database
    boolean existsByUsername(String username);
}
