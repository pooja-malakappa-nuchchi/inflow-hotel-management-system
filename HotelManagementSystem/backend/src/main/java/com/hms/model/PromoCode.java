package com.hms.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents promotional discount codes.
 * Supports percentage or fixed discounts with membership requirements and usage limits.
 */
@Entity
@Table(name = "promo_codes")
public class PromoCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;  // Promo code string (e.g., "AAA2024", "SUMMER20")

    private String description;  // Description of the promotion

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;  // Percentage or fixed amount

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;  // Discount value (10 for 10% or $10)

    @Column(name = "min_stay_nights")
    private Integer minStayNights = 1;  // Minimum nights required to use promo

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;  // When promo becomes active

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;  // When promo expires

    @Column(name = "max_uses")
    private Integer maxUses;  // Maximum times code can be used (null = unlimited)

    @Column(name = "current_uses")
    private Integer currentUses = 0;  // How many times code has been used

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type")
    private MembershipType membershipType = MembershipType.NONE;  // Required membership

    @Column(name = "is_active")
    private Boolean isActive = true;  // Whether promo is currently active

    /**
     * How discount is calculated.
     */
    public enum DiscountType {
        PERCENTAGE,    // Discount as percentage (e.g., 10%)
        FIXED_AMOUNT   // Fixed dollar amount (e.g., $50)
    }

    /**
     * Required membership types for special rates.
     */
    public enum MembershipType {
        AAA,        // AAA auto club members
        AARP,       // Senior citizens (AARP)
        MILITARY,   // Military and veterans
        CORPORATE,  // Corporate accounts
        GOVERNMENT, // Government employees
        NONE        // No membership required
    }

    // Default constructor
    public PromoCode() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getMinStayNights() {
        return minStayNights;
    }

    public void setMinStayNights(Integer minStayNights) {
        this.minStayNights = minStayNights;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getCurrentUses() {
        return currentUses;
    }

    public void setCurrentUses(Integer currentUses) {
        this.currentUses = currentUses;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}