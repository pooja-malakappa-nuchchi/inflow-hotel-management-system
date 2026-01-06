package com.hms.service;

import com.hms.config.JwtUtils;
import com.hms.model.User;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user authentication operations.
 * Handles user registration and login with JWT token generation.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Registers a new user account.
     * Encrypts password and assigns default role if not specified.
     */
    public String register(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(User.Role.STAFF);
        }
        
        // Save user to database
        userRepository.save(user);
        return "User registered successfully";
    }

    /**
     * Authenticates user and generates JWT token.
     * Validates credentials and returns token for API access.
     */
    public String login(String email, String password) {
        // Authenticate user with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        
        // Store authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Find user in database
        User user = userRepository.findByEmail(email).orElseThrow();
        
        // Generate and return JWT token
        return jwtUtils.generateToken(org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build());
    }
}