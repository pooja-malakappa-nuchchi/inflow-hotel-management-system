package com.hms.service;

import com.hms.model.Room;
import com.hms.model.Booking;
import com.hms.repository.RoomRepository;
import com.hms.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing hotel rooms.
 * Handles room operations and availability checking.
 */
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Retrieves all rooms in the hotel.
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Gets a specific room by ID.
     */
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    /**
     * Adds a new room to the hotel.
     * Validates room number is unique.
     */
    public Room addRoom(Room room) {
        // Check for duplicate room number
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }
        return roomRepository.save(room);
    }

    /**
     * Updates room information.
     */
    public Room updateRoom(Long id, Room roomDetails) {
        // Find existing room
        Room room = getRoomById(id);
        
        // Update room properties
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setType(roomDetails.getType());
        room.setPrice(roomDetails.getPrice());
        room.setStatus(roomDetails.getStatus());
        room.setDescription(roomDetails.getDescription());
        
        return roomRepository.save(room);
    }

    /**
     * Deletes a room from the system.
     */
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    /**
     * Finds available rooms for specific dates and optional room type.
     * Checks room status and verifies no booking conflicts exist.
     */
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Room.RoomType type) {
        // Get all rooms
        List<Room> allRooms = roomRepository.findAll();

        // Filter rooms based on availability criteria
        return allRooms.stream()
                .filter(room -> {
                    // 1. Room must have AVAILABLE status
                    if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
                        return false;
                    }

                    // 2. Filter by room type if specified
                    if (type != null && room.getType() != type) {
                        return false;
                    }

                    // 3. Check for booking conflicts in date range
                    List<Booking> conflicts = bookingRepository.findBookingsInDateRange(
                            room.getId(),
                            checkIn,
                            checkOut);
                    
                    // Room is available if no conflicts exist
                    return conflicts.isEmpty();
                })
                .collect(Collectors.toList());
    }
}