package com.hms.controller;

import com.hms.model.Payment;
import com.hms.service.EnhancedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Handles payment processing, refunds, and promo code validation.
 * Manages all financial transactions for bookings.
 */
@RestController
@RequestMapping("/api/payments")
public class EnhancedPaymentController {

    @Autowired
    private EnhancedPaymentService paymentService;

    /**
     * Creates a new payment record.
     */
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        try {
            // Process and save the payment
            Payment createdPayment = paymentService.createPayment(payment);
            return ResponseEntity.ok(createdPayment);
        } catch (Exception e) {
            // Return error if payment creation fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validates a promo code and calculates discount.
     */
    @PostMapping("/validate-promo")
    public ResponseEntity<?> validatePromoCode(@RequestBody Map<String, Object> request) {
        try {
            // Extract promo code and booking amount from request
            String code = (String) request.get("code");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            // Validate code and calculate discounted amount
            Map<String, Object> result = paymentService.validatePromoCode(code, amount);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Return error if code is invalid or expired
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Processes a refund for a payment.
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<?> processRefund(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            // Extract refund amount and reason
            BigDecimal refundAmount = new BigDecimal(request.get("refundAmount").toString());
            String reason = (String) request.get("reason");

            // Process the refund and update payment status
            Payment payment = paymentService.processRefund(id, refundAmount, reason);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            // Return error if refund fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves all payments for a specific booking.
     */
    @GetMapping("/booking/{bookingId}")
    public List<Payment> getPaymentsByBooking(@PathVariable Long bookingId) {
        // Fetch all payments linked to the booking
        return paymentService.getPaymentsByBooking(bookingId);
    }

    /**
     * Updates an existing payment record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        try {
            // Update payment details
            Payment updated = paymentService.updatePayment(id, payment);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            // Return error if payment not found or update fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Deletes a payment record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try {
            // Remove payment from database
            paymentService.deletePayment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Return error if payment not found
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}