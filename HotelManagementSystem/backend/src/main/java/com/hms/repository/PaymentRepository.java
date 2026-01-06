package com.hms.repository;

import com.hms.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for managing payment transactions.
 * Provides database queries for payment records and booking payments.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Finds all payments associated with a specific booking.
     * Used to retrieve payment history for a reservation.
     */
    List<Payment> findByBookingId(Long bookingId);
}