package com.hms.repository;

import com.hms.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for managing hotel rooms.
 * Provides database queries.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    /**
     * Finds all rooms with a specific status (AVAILABLE, BOOKED, MAINTENANCE).
     */
    List<Room> findByStatus(Room.RoomStatus status);

    /**
     * Finds all rooms of a specific type (SINGLE, DOUBLE, SUITE).
     */
    List<Room> findByType(Room.RoomType type);

    /**
     * Checks if a room number already exists in the database.
     * Used to prevent duplicate room numbers when creating new rooms.
     */
    boolean existsByRoomNumber(String roomNumber);
}