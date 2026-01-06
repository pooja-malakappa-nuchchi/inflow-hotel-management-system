package com.hms.service;

import com.hms.model.Booking;
import com.hms.model.Room;
import com.hms.repository.BookingRepository;
import com.hms.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RoomService.
 * Tests room availability checking with various scenarios.
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room1;
    private Room room2;

    /**
     * Sets up test rooms before each test.
     */
    @BeforeEach
    void setUp() {
        // Available single room
        room1 = new Room(1L, "101", Room.RoomType.SINGLE, new BigDecimal("100.00"), Room.RoomStatus.AVAILABLE,
                "Single Room");
        
        // Double room under maintenance (not available)
        room2 = new Room(2L, "102", Room.RoomType.DOUBLE, new BigDecimal("150.00"), Room.RoomStatus.MAINTENANCE,
                "Double Room");
    }

    /**
     * Tests finding available rooms when there are no booking conflicts.
     * Should return rooms with AVAILABLE status and no overlapping bookings.
     */
    @Test
    void testFindAvailableRooms_NoConflicts() {
        // Arrange - Set up date range
        LocalDate checkIn = LocalDate.of(2023, 10, 1);
        LocalDate checkOut = LocalDate.of(2023, 10, 5);

        // Mock repository responses
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));
        // Room 1 has no booking conflicts
        when(bookingRepository.findBookingsInDateRange(1L, checkIn, checkOut)).thenReturn(Collections.emptyList());

        // Act - Search for available rooms
        List<Room> result = roomService.findAvailableRooms(checkIn, checkOut, null);

        // Assert - Only room1 should be available (room2 is in maintenance)
        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getRoomNumber());
    }

    /**
     * Tests finding available rooms when there are booking conflicts.
     * Should exclude rooms that have overlapping bookings.
     */
    @Test
    void testFindAvailableRooms_WithConflicts() {
        // Arrange - Set up date range
        LocalDate checkIn = LocalDate.of(2023, 10, 1);
        LocalDate checkOut = LocalDate.of(2023, 10, 5);

        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1));
        // Room 1 has a conflicting booking
        when(bookingRepository.findBookingsInDateRange(1L, checkIn, checkOut)).thenReturn(Arrays.asList(new Booking()));

        // Act - Search for available rooms
        List<Room> result = roomService.findAvailableRooms(checkIn, checkOut, null);

        // Assert - No rooms should be available due to conflict
        assertTrue(result.isEmpty());
    }

    /**
     * Tests room type filtering.
     * Should only return rooms matching the specified type.
     */
    @Test
    void testFindAvailableRooms_WithTypeFilter() {
        // Arrange - Set up date range and type filter
        LocalDate checkIn = LocalDate.of(2023, 10, 1);
        LocalDate checkOut = LocalDate.of(2023, 10, 5);

        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1));

        // Act - Search for DOUBLE rooms only
        List<Room> result = roomService.findAvailableRooms(checkIn, checkOut, Room.RoomType.DOUBLE);

        // Assert - No results because room1 is SINGLE type
        assertTrue(result.isEmpty(), "Should be empty because room1 is SINGLE and we asked for DOUBLE");
    }
}