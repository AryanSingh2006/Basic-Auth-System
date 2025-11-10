package com.example.Basic_Auth_System.Model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Converts our database User object into a format Spring Security can authenticate with
// Provides username, password, and role information for login validation
public class UserPrincipal implements UserDetails {

  private final User user;

  public UserPrincipal(User user) {
    this.user = user;
  }

  @Override
  // Converts user role (USER/ADMIN) to Spring Security format (ROLE_USER/ROLE_ADMIN)
  // Used for authorization checks like @PreAuthorize("hasRole('ADMIN')")
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  // Account status checks - all return true meaning no restrictions on the user
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
