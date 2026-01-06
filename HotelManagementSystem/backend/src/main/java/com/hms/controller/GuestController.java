package com.hms.controller;

import com.hms.model.Guest;
import com.hms.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Manages guest information and records.
 * Handles CRUD operations for hotel guests.
 */
@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    /**
     * Retrieves all registered guests.
     */
    @GetMapping
    public List<Guest> getAllGuests() {
        // Fetch and return all guest records
        return guestService.getAllGuests();
    }

    /**
     * Registers a new guest.
     */
    @PostMapping
    public Guest addGuest(@RequestBody Guest guest) {
        // Save guest information to database
        return guestService.addGuest(guest);
    }

    /**
     * Updates guest information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guest) {
        try {
            // Update guest details with new information
            Guest updated = guestService.updateGuest(id, guest);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Return 404 if guest not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a guest record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        try {
            // Remove guest from database
            guestService.deleteGuest(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Return 404 if guest doesn't exist
            return ResponseEntity.notFound().build();
        }
    }
}