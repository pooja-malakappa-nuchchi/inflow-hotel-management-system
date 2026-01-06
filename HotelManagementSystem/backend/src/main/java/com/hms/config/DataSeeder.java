package com.hms.config;

import com.hms.model.User;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates default users in the database when the application starts.
 * Useful for testing and having ready-to-use login accounts.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Runs automatically when application starts.
     * Creates default users for each role if they don't exist.
     */
    @Override
    public void run(String... args) throws Exception {
        // Create default users for each role
        seedUser("admin@hms.com", "admin123", "Admin User", User.Role.ADMIN);
        seedUser("manager@hms.com", "manager123", "Manager User", User.Role.MANAGER);
        seedUser("reception@hms.com", "reception123", "Receptionist User", User.Role.RECEPTIONIST);
        seedUser("housekeeping@hms.com", "housekeeping123", "Housekeeping User", User.Role.HOUSEKEEPING);
    }

    /**
     * Creates a user in the database if they don't already exist.
     */
    private void seedUser(String email, String password, String name, User.Role role) {
        // Check if user already exists to avoid duplicates
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));  // Encrypt password before saving
            user.setName(name);
            user.setRole(role);
            userRepository.save(user);  // Save to database
            System.out.println("Seeded user: " + email);
        }
    }
}