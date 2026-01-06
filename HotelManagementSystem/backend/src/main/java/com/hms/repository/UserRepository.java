package com.hms.repository;

import com.hms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for managing system users.
 * Provides database queries for authentication and user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     * Used for login authentication.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if an email already exists in the database.
     * Used to prevent duplicate email registrations.
     */
    Boolean existsByEmail(String email);
}