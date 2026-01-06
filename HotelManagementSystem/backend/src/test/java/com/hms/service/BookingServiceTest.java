package com.hms.service;

import com.hms.model.Booking;
import com.hms.model.Guest;
import com.hms.model.Room;
import com.hms.repository.BookingRepository;
import com.hms.repository.GuestRepository;
import com.hms.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService.
 * Tests booking creation, dynamic pricing, and conflict detection.
 */
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private Guest guest;

    /**
     * Sets up test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Create test room with base price $100
        room = new Room(1L, "101", Room.RoomType.SINGLE, new BigDecimal("100.00"), Room.RoomStatus.AVAILABLE,
                "Single Room");
        
        // Create test guest
        guest = new Guest();
        guest.setId(1L);
        guest.setName("John Doe");
        guest.setEmail("john@example.com");
    }

    /**
     * Tests dynamic pricing for weekdays (no surcharge).
     * Monday to Wednesday = 2 nights at base price.
     */
    @Test
    void testCalculateDynamicPrice_Weekdays() {
        // Check-in Monday, check-out Wednesday (2 nights)
        LocalDate checkIn = LocalDate.of(2023, 10, 2); // Monday
        LocalDate checkOut = LocalDate.of(2023, 10, 4); // Wednesday

        BigDecimal price = bookingService.calculateDynamicPrice(room, checkIn, checkOut);

        // Expected: 100 * 2 = 200
        assertEquals(new BigDecimal("200.00"), price);
    }

    /**
     * Tests dynamic pricing for weekend nights (20% surcharge).
     * Friday and Saturday nights get 20% increase.
     */
    @Test
    void testCalculateDynamicPrice_Weekend() {
        // Check-in Friday, check-out Sunday (Friday and Saturday nights)
        LocalDate checkIn = LocalDate.of(2023, 10, 6); // Friday
        LocalDate checkOut = LocalDate.of(2023, 10, 8); // Sunday

        BigDecimal price = bookingService.calculateDynamicPrice(room, checkIn, checkOut);

        // Expected: Friday (100 * 1.2) + Saturday (100 * 1.2) = 240
        assertTrue(price.compareTo(new BigDecimal("240.00")) == 0, "Price should be 240.00");
    }

    /**
     * Tests successful booking creation.
     * Verifies booking is confirmed and email is sent.
     */
    @Test
    void testCreateBooking_Success() {
        // Create booking for tomorrow
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(2));

        // Mock repository responses
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findBookingsInDateRange(any(), any(), any())).thenReturn(Collections.emptyList());
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create booking
        Booking created = bookingService.createBooking(booking);

        // Verify booking was created successfully
        assertNotNull(created);
        assertEquals(Booking.BookingStatus.CONFIRMED, created.getStatus());
        
        // Verify confirmation email was sent
        verify(emailService, times(1)).sendBookingConfirmation(anyString(), anyString(), any());
    }

    /**
     * Tests booking creation with date conflict.
     * Should throw exception when room is already booked.
     */
    @Test
    void testCreateBooking_Conflict() {
        // Create booking with conflicting dates
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(1));

        // Mock repository to return existing conflicting booking
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findBookingsInDateRange(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Booking()));

        // Verify exception is thrown for conflict
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));
    }
}