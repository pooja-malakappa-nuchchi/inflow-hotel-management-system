package com.hms.service;

import com.hms.model.Guest;
import com.hms.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for managing guest records.
 * Handles guest registration, updates, and profile management.
 */
@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    /**
     * Retrieves all registered guests.
     */
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    /**
     * Registers a new guest.
     */
    public Guest addGuest(Guest guest) {
        return guestRepository.save(guest);
    }

    /**
     * Updates guest information.
     */
    public Guest updateGuest(Long id, Guest guestDetails) {
        // Find existing guest
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        
        // Update guest details
        guest.setName(guestDetails.getName());
        guest.setPhone(guestDetails.getPhone());
        guest.setEmail(guestDetails.getEmail());
        guest.setAddress(guestDetails.getAddress());
        
        return guestRepository.save(guest);
    }

    /**
     * Deletes a guest record.
     */
    public void deleteGuest(Long id) {
        // Find and delete guest
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        guestRepository.delete(guest);
    }
}