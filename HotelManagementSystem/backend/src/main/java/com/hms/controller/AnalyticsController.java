package com.hms.controller;

import com.hms.service.DashboardService;
import com.hms.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Handles hotel analytics and reporting endpoints.
 * Provides revenue tracking, occupancy metrics, and performance insights.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Returns quick summary stats for the dashboard.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        // Fetch analytics data from dashboard service
        return ResponseEntity.ok(dashboardService.getAnalytics());
    }

    /**
     * Returns comprehensive analytics for a date range.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Get full analytics for specified date range
            Map<String, Object> analytics = analyticsService.getHotelAnalytics(startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            // Return error if dates are invalid or query fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Returns daily revenue breakdown for trend analysis.
     */
    @GetMapping("/daily-revenue")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Fetch revenue data for each day in the range
            List<Map<String, Object>> dailyRevenue = analyticsService.getDailyRevenue(startDate, endDate);
            return ResponseEntity.ok(dailyRevenue);
        } catch (Exception e) {
            // Return 400 Bad Request on error
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Returns performance metrics for each room type.
     */
    @GetMapping("/room-performance")
    public ResponseEntity<List<Map<String, Object>>> getRoomTypePerformance() {
        try {
            // Get booking and revenue stats per room type
            List<Map<String, Object>> performance = analyticsService.getRoomTypePerformance();
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Compares current month performance with previous month.
     */
    @GetMapping("/monthly-comparison")
    public ResponseEntity<Map<String, Object>> getMonthlyComparison(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            // Get comparison data between specified month and previous month
            Map<String, Object> comparison = analyticsService.getMonthlyComparison(year, month);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            // Return error message if comparison fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}