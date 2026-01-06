package com.hms.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment transaction for a booking.
 * Handles tax breakdown, discounts, refunds, and payment tracking.
 */
@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;  // The booking this payment is for

    private BigDecimal amount;  // Total amount paid

    // Tax Breakdown (U.S. Compliant)
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;  // Base room rate

    @Column(name = "state_tax", precision = 10, scale = 2)
    private BigDecimal stateTax = BigDecimal.ZERO;  // State hotel tax

    @Column(name = "county_tax", precision = 10, scale = 2)
    private BigDecimal countyTax = BigDecimal.ZERO;  // County tax

    @Column(name = "city_tax", precision = 10, scale = 2)
    private BigDecimal cityTax = BigDecimal.ZERO;  // City/local tax

    @Column(name = "resort_fee", precision = 10, scale = 2)
    private BigDecimal resortFee = BigDecimal.ZERO;  // Resort/facility fee

    @Column(name = "service_charge", precision = 10, scale = 2)
    private BigDecimal serviceCharge = BigDecimal.ZERO;  // Service charge

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;  // Total discount applied

    @Column(name = "promo_code")
    private String promoCode;  // Promo code used (if any)

    // Invoice & Payment Tracking
    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;  // Unique invoice identifier

    @Column(name = "invoice_pdf_path")
    private String invoicePdfPath;  // Path to generated invoice PDF

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;  // Stripe transaction ID

    private LocalDateTime paymentDate;  // When payment was made

    // Refund Support
    @Column(name = "refund_date")
    private LocalDateTime refundDate;  // When refund was processed

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;  // Amount refunded

    @Column(name = "refund_reason")
    private String refundReason;  // Why refund was issued

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;  // How payment was made

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;  // Current payment status

    /**
     * Automatically sets payment date when created.
     */
    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }

    /**
     * Available payment methods.
     */
    public enum PaymentMethod {
        CARD,   // Credit/debit card
        CASH,   // Cash payment
        UPI,    // UPI/digital wallet
        CHECK,  // Check payment
        ACH,    // ACH bank transfer
        WIRE    // Wire transfer
    }

    /**
     * Payment processing statuses.
     */
    public enum PaymentStatus {
        PENDING,     // Awaiting payment
        AUTHORIZED,  // Card authorized, not captured
        CAPTURED,    // Payment captured (card charged)
        PAID,        // Payment completed
        REFUNDED,    // Payment refunded
        FAILED       // Payment failed
    }

    // Default constructor
    public Payment() {
    }

    // Basic constructor
    public Payment(Long id, Booking booking, BigDecimal amount, LocalDateTime paymentDate, PaymentMethod method,
            PaymentStatus status) {
        this.id = id;
        this.booking = booking;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getStateTax() {
        return stateTax;
    }

    public void setStateTax(BigDecimal stateTax) {
        this.stateTax = stateTax;
    }

    public BigDecimal getCountyTax() {
        return countyTax;
    }

    public void setCountyTax(BigDecimal countyTax) {
        this.countyTax = countyTax;
    }

    public BigDecimal getCityTax() {
        return cityTax;
    }

    public void setCityTax(BigDecimal cityTax) {
        this.cityTax = cityTax;
    }

    public BigDecimal getResortFee() {
        return resortFee;
    }

    public void setResortFee(BigDecimal resortFee) {
        this.resortFee = resortFee;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoicePdfPath() {
        return invoicePdfPath;
    }

    public void setInvoicePdfPath(String invoicePdfPath) {
        this.invoicePdfPath = invoicePdfPath;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    // Compare payments by ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", booking=" + booking +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}