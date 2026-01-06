package com.hms.controller;

import com.hms.model.User;
import com.hms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Manages user accounts and roles.
 * Admin-only access for creating and managing staff users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves all system users.
     */
    @GetMapping
    public List<User> getAllUsers() {
        // Fetch all users (admin, manager, receptionist, housekeeping)
        return userService.getAllUsers();
    }

    /**
     * Creates a new user account.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Create user with specified role and encrypted password
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            // Return error if email already exists or validation fails
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates user information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // Update user details (name, email, role, password)
            User updated = userService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Return 404 if user not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a user account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Remove user from system
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Return 404 if user doesn't exist
            return ResponseEntity.notFound().build();
        }
    }
}