package com.hms.repository;

import com.hms.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for managing bookings.
 * Provides database queries for reservations, availability checks, and date ranges.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Finds all bookings for a specific guest.
     */
    List<Booking> findByGuestId(Long guestId);

    /**
     * Finds all bookings for a specific room.
     */
    List<Booking> findByRoomId(Long roomId);

    /**
     * Finds bookings by status (PENDING, CONFIRMED, CANCELLED).
     */
    List<Booking> findByStatus(Booking.BookingStatus status);

    /**
     * Finds bookings that overlap with the given date range for a specific room.
     * Used to check if a room is available during the requested dates.
     * Overlap logic: (StartA < EndB) AND (EndA > StartB)
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status <> 'CANCELLED' " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Booking> findBookingsInDateRange(@Param("roomId") Long roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);

    /**
     * Finds bookings where check-in date falls within the specified range.
     */
    List<Booking> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate);
}