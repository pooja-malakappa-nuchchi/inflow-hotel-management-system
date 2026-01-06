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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DashboardService.
 * Tests analytics calculations, metrics, and chart data generation.
 */
@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private DashboardService dashboardService;

    /**
     * Tests comprehensive dashboard analytics generation.
     * Verifies metrics, revenue calculation, and chart data.
     */
    @Test
    void testGetAnalytics() {
        // Arrange - Create test data
        
        // Room 1: Booked single room
        Room room1 = new Room();
        room1.setType(Room.RoomType.SINGLE);
        room1.setStatus(Room.RoomStatus.BOOKED);

        // Room 2: Available double room
        Room room2 = new Room();
        room2.setType(Room.RoomType.DOUBLE);
        room2.setStatus(Room.RoomStatus.AVAILABLE);

        // Booking with $100 revenue
        Booking booking = new Booking();
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setTotalAmount(new BigDecimal("100.00"));
        booking.setCheckInDate(LocalDate.now());
        booking.setCreatedAt(LocalDateTime.now());

        // Mock repository responses
        when(roomRepository.count()).thenReturn(2L);
        when(bookingRepository.count()).thenReturn(1L);
        when(roomRepository.findByStatus(Room.RoomStatus.AVAILABLE)).thenReturn(Arrays.asList(room2));
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking));
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));

        // Act - Generate analytics
        Map<String, Object> stats = dashboardService.getAnalytics();

        // Assert - Verify basic metrics
        assertEquals(2L, stats.get("totalRooms"));
        assertEquals(1L, stats.get("totalBookings"));
        assertEquals(1L, stats.get("availableRooms"));
        assertEquals(new BigDecimal("100.00"), stats.get("totalRevenue"));

        // Verify revenue chart data (last 7 days)
        List<Map<String, Object>> revenueChart = (List<Map<String, Object>>) stats.get("revenueChart");
        assertNotNull(revenueChart);
        assertEquals(7, revenueChart.size(), "Should have 7 days of revenue data");

        // Verify occupancy chart data
        List<Map<String, Object>> occupancyChart = (List<Map<String, Object>>) stats.get("occupancyChart");
        assertNotNull(occupancyChart);
        
        // Check SINGLE room is in chart (1 booked)
        assertTrue(occupancyChart.stream().anyMatch(m -> 
                m.get("name").equals("SINGLE") && (long) m.get("value") == 1),
                "Should show 1 booked SINGLE room");
        
        // Check AVAILABLE rooms are in chart (1 available)
        assertTrue(occupancyChart.stream().anyMatch(m -> 
                m.get("name").equals("AVAILABLE") && (long) m.get("value") == 1),
                "Should show 1 available room");
    }
}