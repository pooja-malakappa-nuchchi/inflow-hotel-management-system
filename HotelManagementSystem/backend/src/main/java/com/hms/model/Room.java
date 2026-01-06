package com.hms.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a hotel room.
 * Tracks room details, type, pricing, and current status.
 */
@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roomNumber;  // Unique room identifier (e.g., "101", "205A")

    @Enumerated(EnumType.STRING)
    private RoomType type;  // Category of room

    private BigDecimal price;  // Nightly rate

    @Enumerated(EnumType.STRING)
    private RoomStatus status;  // Current availability status

    private String description;  // Room features and amenities

    /**
     * Different room categories available.
     */
    public enum RoomType {
        SINGLE,     // Single bed room
        DOUBLE,     // Double bed room
        SUITE,      // Luxury suite
        DORMITORY   // Shared dormitory
    }

    /**
     * Current status of the room.
     */
    public enum RoomStatus {
        AVAILABLE,   // Ready for booking
        BOOKED,      // Currently occupied by guest
        MAINTENANCE, // Under repair/maintenance
        CLEANING     // Being cleaned by housekeeping
    }

    // Default constructor
    public Room() {
    }

    // Full constructor
    public Room(Long id, String roomNumber, RoomType type, BigDecimal price, RoomStatus status, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Compare rooms by ID and room number
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) && Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}