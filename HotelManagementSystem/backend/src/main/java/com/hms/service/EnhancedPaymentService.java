package com.hms.service;

import com.hms.model.*;
import com.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for payment processing with promo codes and refunds.
 * Handles payment creation, validation, discounts, and refund processing.
 */
@Service
public class EnhancedPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    /**
     * Calculates discount amount based on promo code type.
     * Supports percentage (e.g., 10%) or fixed amount (e.g., $50) discounts.
     */
    private BigDecimal calculateDiscount(BigDecimal amount, PromoCode promo) {
        if (promo.getDiscountType() == PromoCode.DiscountType.PERCENTAGE) {
            // Calculate percentage discount
            return amount.multiply(promo.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            // Return fixed amount discount
            return promo.getDiscountValue();
        }
    }

    /**
     * Generates unique invoice number with date and random suffix.
     * Format: INV-YYYYMMDD-XXXX (e.g., INV-20241210-A3F2)
     */
    private String generateInvoiceNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "INV-" + date + "-" + uuid;
    }

    /**
     * Validates promo code and calculates discount without applying it.
     * Returns discount details if valid, error message if invalid.
     */
    public Map<String, Object> validatePromoCode(String code, BigDecimal amount) {
        try {
            // Find and validate promo code
            PromoCode promo = promoCodeRepository
                    .findValidPromoCode(code, LocalDate.now())
                    .orElseThrow(() -> new RuntimeException("Invalid promo code"));

            // Calculate discount amount
            BigDecimal discount = calculateDiscount(amount, promo);

            // Return validation result
            return Map.of(
                    "valid", true,
                    "discountAmount", discount,
                    "description", promo.getDescription(),
                    "membershipType", promo.getMembershipType().toString());
        } catch (Exception e) {
            // Return error if code is invalid
            return Map.of("valid", false, "message", e.getMessage());
        }
    }

    /**
     * Processes a refund for a payment.
     * Updates payment status and records refund details.
     */
    @Transactional
    public Payment processRefund(Long paymentId, BigDecimal refundAmount, String reason) {
        // Find payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Verify payment can be refunded
        if (payment.getStatus() != Payment.PaymentStatus.PAID &&
                payment.getStatus() != Payment.PaymentStatus.CAPTURED) {
            throw new RuntimeException("Payment must be in PAID or CAPTURED status to refund");
        }

        // Record refund details
        payment.setRefundAmount(refundAmount);
        payment.setRefundDate(LocalDateTime.now());
        payment.setRefundReason(reason);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);

        return paymentRepository.save(payment);
    }

    /**
     * Retrieves all payments for a specific booking.
     */
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    /**
     * Updates payment information.
     */
    public Payment updatePayment(Long id, Payment paymentDetails) {
        // Find existing payment
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Update payment fields
        payment.setAmount(paymentDetails.getAmount());
        payment.setMethod(paymentDetails.getMethod());
        payment.setStatus(paymentDetails.getStatus());

        return paymentRepository.save(payment);
    }

    /**
     * Deletes a payment record.
     */
    public void deletePayment(Long id) {
        // Find and delete payment
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
    }

    /**
     * Creates a new payment 
     * Generates invoice number and sets payment date automatically.
     */
    @Transactional
    public Payment createPayment(Payment paymentRequest) {
        // Validate booking exists
        Booking booking = bookingRepository.findById(paymentRequest.getBooking().getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Create new payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setSubtotal(paymentRequest.getAmount());  // Set subtotal same as amount
        payment.setMethod(paymentRequest.getMethod());
        payment.setStatus(paymentRequest.getStatus() != null ? paymentRequest.getStatus() : Payment.PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setInvoiceNumber(generateInvoiceNumber());  // Auto-generate invoice number

        return paymentRepository.save(payment);
    }
}