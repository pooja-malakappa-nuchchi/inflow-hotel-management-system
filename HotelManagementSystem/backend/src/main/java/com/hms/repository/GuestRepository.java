package com.hms.repository;

import com.hms.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for managing guest records.
 * Provides database queries for guest lookup and registration.
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    
    /**
     * Finds a guest by phone number.
     */
    Optional<Guest> findByPhone(String phone);
    
    /**
     * Finds a guest by email address.
     */
    Optional<Guest> findByEmail(String email);
}