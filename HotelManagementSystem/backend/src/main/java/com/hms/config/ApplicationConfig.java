package com.hms.config;

import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for user authentication and password handling.
 * Sets up how users log in and how passwords are verified.
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details from database when someone tries to log in.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                // Convert our User to Spring Security's User format
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())  // Set user role (ADMIN, USER, etc.)
                        .build())
                // Throw error if user not found
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Connects user lookup with password verification for login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());  // How to find users
        authProvider.setPasswordEncoder(passwordEncoder());  // How to check passwords
        return authProvider;
    }

    /**
     * Main authentication manager that handles login requests.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encrypts passwords using BCrypt (one-way encryption for security).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}