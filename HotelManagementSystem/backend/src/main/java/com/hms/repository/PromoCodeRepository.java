package com.hms.repository;

import com.hms.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for managing promotional codes.
 * Provides database queries for promo code validation and retrieval.
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    
    /**
     * Finds an active promo code by its code string.
     */
    Optional<PromoCode> findByCodeAndIsActiveTrue(String code);

    /**
     * Validates a promo code for a specific date.
     * Checks if the code is active, within valid date range, and hasn't exceeded usage limit.
     */
    default Optional<PromoCode> findValidPromoCode(String code, LocalDate date) {
        return findByCodeAndIsActiveTrue(code)
                // Check if date is within valid range
                .filter(promo -> !date.isBefore(promo.getValidFrom()) &&
                        !date.isAfter(promo.getValidUntil()))
                // Check if usage limit hasn't been reached
                .filter(promo -> promo.getMaxUses() == null ||
                        promo.getCurrentUses() < promo.getMaxUses());
    }
}