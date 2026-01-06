package com.hms.service;

import com.hms.model.Booking;
import com.hms.model.Room;
import com.hms.model.Guest;
import com.hms.repository.BookingRepository;
import com.hms.repository.GuestRepository;
import com.hms.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing hotel bookings.
 * Handles reservation creation, validation, dynamic pricing, and cancellations.
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Retrieves all bookings in the system.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Creates a new booking with validation and dynamic pricing.
     * Checks room availability, calculates total cost, and sends confirmation email.
     */
    public Booking createBooking(Booking booking) {
        // Validate room exists
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check for date conflicts with existing bookings
        List<Booking> conflicts = bookingRepository.findBookingsInDateRange(
                room.getId(), 
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Update guest email if provided
        if (booking.getGuest() != null && booking.getGuest().getId() != null) {
            Guest guest = guestRepository.findById(booking.getGuest().getId())
                    .orElseThrow(() -> new RuntimeException("Guest not found"));

            // Update email if new one is provided
            if (booking.getGuest().getEmail() != null && !booking.getGuest().getEmail().isEmpty()) {
                guest.setEmail(booking.getGuest().getEmail());
                guestRepository.save(guest);
            }
            booking.setGuest(guest);
        }

        // Calculate total with dynamic pricing (weekends cost more)
        BigDecimal totalAmount = calculateDynamicPrice(room, booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setTotalAmount(totalAmount);

        // Mark room as booked only if check-in is today
        if (booking.getCheckInDate().equals(LocalDate.now())) {
            room.setStatus(Room.RoomStatus.BOOKED);
            roomRepository.save(room);
        }

        // Confirm booking and save
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        // Send confirmation email to guest
        if (savedBooking.getGuest() != null && savedBooking.getGuest().getEmail() != null) {
            emailService.sendBookingConfirmation(
                    savedBooking.getGuest().getEmail(), 
                    savedBooking.getGuest().getName(),
                    savedBooking);
        }

        return savedBooking;
    }

    /**
     * Calculates booking price with dynamic pricing.
     * Base price per night + 20% surcharge for weekends (Friday & Saturday).
     */
    public BigDecimal calculateDynamicPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal basePrice = room.getPrice();

        // Calculate price for each night of the stay
        LocalDate current = checkIn;
        while (current.isBefore(checkOut)) {
            BigDecimal dailyPrice = basePrice;

            // Apply weekend surcharge (Friday and Saturday nights)
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
                dailyPrice = dailyPrice.multiply(new BigDecimal("1.20"));  // 20% increase
            }

            total = total.add(dailyPrice);
            current = current.plusDays(1);
        }

        return total;
    }

    /**
     * Cancels a booking and frees up the room.
     */
    public void cancelBooking(Long id) {
        // Find booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);

        // Make room available again
        Room room = booking.getRoom();
        room.setStatus(Room.RoomStatus.AVAILABLE);
        roomRepository.save(room);

        // Save cancelled booking
        bookingRepository.save(booking);
    }

    /**
     * Updates booking details.
     */
    public Booking updateBooking(Long id, Booking bookingDetails) {
        // Find existing booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Update booking information
        booking.setCheckInDate(bookingDetails.getCheckInDate());
        booking.setCheckOutDate(bookingDetails.getCheckOutDate());
        booking.setTotalAmount(bookingDetails.getTotalAmount());
        booking.setStatus(bookingDetails.getStatus());
        
        return bookingRepository.save(booking);
    }

    /**
     * Permanently deletes a booking.
     */
    public void deleteBooking(Long id) {
        // Find and delete booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.delete(booking);
    }
}