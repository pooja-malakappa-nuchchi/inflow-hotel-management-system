package com.hms.service;

import com.hms.model.User;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for managing system users and staff accounts.
 * Handles user CRUD operations with password encryption and activity logging.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * Retrieves all system users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Creates a new user account.
     * Validates email uniqueness and encrypts password.
     */
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Log the creation activity
        activityLogService.logActivity("CREATE_USER",
                "Created user: " + user.getEmail() + " with role: " + user.getRole());
        
        return savedUser;
    }

    /**
     * Updates user information.
     * Only updates password if a new one is provided.
     */
    public User updateUser(Long id, User userDetails) {
        // Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update user details
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        // Update password only if provided (not empty)
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        
        // Log the update activity
        activityLogService.logActivity("UPDATE_USER", "Updated user: " + user.getEmail());
        
        return updatedUser;
    }

    /**
     * Deletes a user account.
     */
    public void deleteUser(Long id) {
        // Find user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete user
        userRepository.delete(user);
        
        // Log the deletion activity
        activityLogService.logActivity("DELETE_USER", "Deleted user: " + user.getEmail());
    }
}