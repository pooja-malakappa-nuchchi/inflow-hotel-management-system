package com.hms.controller;

import com.hms.model.Booking;
import com.hms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Manages hotel booking operations.
 * Handles creating, viewing, updating, and canceling reservations.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Retrieves all bookings in the system.
     */
    @GetMapping
    public List<Booking> getAllBookings() {
        // Fetch and return all bookings
        return bookingService.getAllBookings();
    }

    /**
     * Creates a new booking reservation.
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            // Create new booking and return it
            Booking created = bookingService.createBooking(booking);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            // Return error message if booking fails (e.g., room not available)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Cancels an existing booking.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        try {
            // Cancel the booking by ID
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Return 404 if booking not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates booking details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {
        try {
            // Update booking with new information
            Booking updated = bookingService.updateBooking(id, booking);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Return 404 if booking doesn't exist
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permanently deletes a booking.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            // Delete booking from database
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Return 404 if booking not found
            return ResponseEntity.notFound().build();
        }
    }
}