package com.example.Basic_Auth_System.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Basic_Auth_System.Service.CustomUserDetailsService;
import com.example.Basic_Auth_System.config.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    //Used to hash password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Perform Authentication using customUserDetailsService and validates credentials
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    //Used to manange the AuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disabled csrf
                .csrf(csrf -> csrf.disable())

                // Allow request from this endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2/**", "/api/auth/**", "/api/logout").permitAll()
                        .anyRequest().authenticated())

                // Default login form is disabled
                .formLogin(form -> form.disable())

                // Allow the H2 console frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // popup login is disable
                .httpBasic(basic -> basic.disable())

                // Custom exception handler for 401 unauthorized and 403 forbidden
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter()
                                    .write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired JWT token\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter()
                                    .write(
                                            "{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource. Admin access required.\"}");
                        }))

                //Use the authenticationProvider that was written above
                .authenticationProvider(authenticationProvider())

                //Add the jwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
