package com.hms.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating U.S. hotel industry standard metrics.
 * Provides key performance indicators (KPIs) used in hospitality revenue management.
 */
@Service
public class HotelMetricsService {

    /**
     * Calculates Average Daily Rate (ADR).
     * Formula: Total Room Revenue ÷ Rooms Sold
     * Shows average rental income per occupied room.
     */
    public BigDecimal calculateADR(BigDecimal totalRoomRevenue, int roomsSold) {
        if (roomsSold == 0)
            return BigDecimal.ZERO;
        return totalRoomRevenue.divide(
                new BigDecimal(roomsSold),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates Revenue Per Available Room (RevPAR).
     * Formula: Total Room Revenue ÷ Total Available Rooms
     * Most important metric combining occupancy and rate performance.
     */
    public BigDecimal calculateRevPAR(BigDecimal totalRoomRevenue, int totalAvailableRooms) {
        if (totalAvailableRooms == 0)
            return BigDecimal.ZERO;
        return totalRoomRevenue.divide(
                new BigDecimal(totalAvailableRooms),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates Revenue Per Occupied Room (RevPOR).
     * Formula: Total Revenue (all sources) ÷ Rooms Sold
     * Shows total guest spending per occupied room including extras.
     */
    public BigDecimal calculateRevPOR(BigDecimal totalRevenue, int roomsSold) {
        if (roomsSold == 0)
            return BigDecimal.ZERO;
        return totalRevenue.divide(
                new BigDecimal(roomsSold),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates Occupancy Rate as percentage.
     * Formula: (Rooms Sold ÷ Total Available Rooms) × 100
     * Industry benchmark: 60-80% depending on market.
     */
    public BigDecimal calculateOccupancyRate(int roomsSold, int totalAvailableRooms) {
        if (totalAvailableRooms == 0)
            return BigDecimal.ZERO;
        return new BigDecimal(roomsSold)
                .divide(new BigDecimal(totalAvailableRooms), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Gross Operating Profit Per Available Room (GOPPAR).
     * Formula: Gross Operating Profit ÷ Total Available Rooms
     * Shows profitability after operating expenses.
     */
    public BigDecimal calculateGOPPAR(BigDecimal grossOperatingProfit, int totalAvailableRooms) {
        if (totalAvailableRooms == 0)
            return BigDecimal.ZERO;
        return grossOperatingProfit.divide(
                new BigDecimal(totalAvailableRooms),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates Total Revenue Per Available Room (TRevPAR).
     * Formula: Total Property Revenue ÷ Total Available Rooms
     * Includes all revenue sources (rooms, F&B, parking, spa, etc.).
     */
    public BigDecimal calculateTRevPAR(BigDecimal totalPropertyRevenue, int totalAvailableRooms) {
        if (totalAvailableRooms == 0)
            return BigDecimal.ZERO;
        return totalPropertyRevenue.divide(
                new BigDecimal(totalAvailableRooms),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates Average Length of Stay (ALOS).
     * Formula: Total Room Nights ÷ Number of Reservations
     * Shows typical guest stay duration.
     */
    public BigDecimal calculateALOS(int totalRoomNights, int numberOfReservations) {
        if (numberOfReservations == 0)
            return BigDecimal.ZERO;
        return new BigDecimal(totalRoomNights)
                .divide(new BigDecimal(numberOfReservations), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Market Penetration Index (MPI).
     * Formula: (Hotel Occupancy ÷ Market Occupancy) × 100
     * > 100 = outperforming market, < 100 = underperforming.
     */
    public BigDecimal calculateMPI(BigDecimal hotelOccupancy, BigDecimal marketOccupancy) {
        if (marketOccupancy.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return hotelOccupancy
                .divide(marketOccupancy, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Revenue Generation Index (RGI).
     * Formula: (Hotel RevPAR ÷ Market RevPAR) × 100
     * Shows revenue performance relative to competition.
     */
    public BigDecimal calculateRGI(BigDecimal hotelRevPAR, BigDecimal marketRevPAR) {
        if (marketRevPAR.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return hotelRevPAR
                .divide(marketRevPAR, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}