package com.hms.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a hotel room booking/reservation.
 * Links a guest to a room for specific dates with booking status.
 */
@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;  // The room being booked

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;  // The guest making the reservation

    private LocalDate checkInDate;  // When guest arrives
    private LocalDate checkOutDate;  // When guest leaves
    private BigDecimal totalAmount;  // Total cost for the stay

    @Enumerated(EnumType.STRING)
    private BookingStatus status;  // Current booking status

    private LocalDateTime createdAt;  // When booking was created

    /**
     * Automatically sets creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Booking status options.
     */
    public enum BookingStatus {
        PENDING,    // Awaiting confirmation
        CONFIRMED,  // Booking confirmed
        CANCELLED   // Booking cancelled
    }

    // Default constructor
    public Booking() {
    }

    // Full constructor
    public Booking(Long id, Room room, Guest guest, LocalDate checkInDate, LocalDate checkOutDate,
            BigDecimal totalAmount, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Compare bookings by ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", room=" + room +
                ", guest=" + guest +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", status=" + status +
                '}';
    }
}