package com.hms.controller;

import com.hms.model.Room;
import com.hms.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Manages hotel room operations.
 * Handles room availability, CRUD operations, and room search.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * Retrieves all rooms in the hotel.
     */
    @GetMapping
    public List<Room> getAllRooms() {
        // Fetch all rooms regardless of status
        return roomService.getAllRooms();
    }

    /**
     * Finds available rooms for specific dates and type.
     */
    @GetMapping("/available")
    public List<Room> getAvailableRooms(
            @RequestParam("checkIn") LocalDate checkIn,
            @RequestParam("checkOut") LocalDate checkOut,
            @RequestParam(value = "type", required = false) Room.RoomType type) {
        // Search for rooms available between check-in and check-out dates
        // Optionally filter by room type (SINGLE, DOUBLE, SUITE, etc.)
        return roomService.findAvailableRooms(checkIn, checkOut, type);
    }

    /**
     * Gets a specific room by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        try {
            // Fetch room details
            Room room = roomService.getRoomById(id);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            // Return 404 if room doesn't exist
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a new room to the hotel.
     */
    @PostMapping
    public Room addRoom(@RequestBody Room room) {
        // Create and save new room
        return roomService.addRoom(room);
    }

    /**
     * Updates room information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        try {
            // Update room details (price, status, features, etc.)
            Room updated = roomService.updateRoom(id, room);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Return 404 if room not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a room from the system.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        // Remove room from database
        roomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }
}