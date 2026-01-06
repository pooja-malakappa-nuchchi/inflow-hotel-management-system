package com.hms.controller;

import com.hms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Handles user authentication endpoints.
 * Manages login requests and JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Authenticates user and returns JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            // Extract email and password from request body
            String email = request.get("email");
            String password = request.get("password");
            
            // Authenticate user and generate JWT token
            String token = authService.login(email, password);
            
            // Return token in response
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            // Return error if credentials are wrong
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
}