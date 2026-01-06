package com.hms.config;

import com.hms.model.PromoCode;
import com.hms.repository.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the database with sample promo codes when the application starts.
 * Creates discount codes for different membership types and special offers.
 */
@Component
@Order(2)  // Runs after DataSeeder (Order 1)
public class PromoCodeSeeder implements CommandLineRunner {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    /**
     * Runs automatically on application startup.
     * Only seeds promo codes if the database is empty.
     */
    @Override
    public void run(String... args) throws Exception {
        // Check if promo codes already exist to avoid duplicates
        if (promoCodeRepository.count() == 0) {
            seedSamplePromoCodes();
        }
    }

    /**
     * Creates sample promo codes for different membership types and promotions.
     */
    private void seedSamplePromoCodes() {
        
        // AAA Member Discount - 10% off
        PromoCode aaa = new PromoCode();
        aaa.setCode("AAA2024");
        aaa.setDescription("AAA Member Discount - 10% off");
        aaa.setDiscountType(PromoCode.DiscountType.PERCENTAGE);
        aaa.setDiscountValue(new BigDecimal("10"));
        aaa.setMembershipType(PromoCode.MembershipType.AAA);
        aaa.setValidFrom(LocalDate.of(2024, 1, 1));
        aaa.setValidUntil(LocalDate.of(2024, 12, 31));
        aaa.setIsActive(true);
        promoCodeRepository.save(aaa);

        // AARP Senior Discount - 15% off
        PromoCode aarp = new PromoCode();
        aarp.setCode("AARP15");
        aarp.setDescription("AARP Senior Discount - 15% off");
        aarp.setDiscountType(PromoCode.DiscountType.PERCENTAGE);
        aarp.setDiscountValue(new BigDecimal("15"));
        aarp.setMembershipType(PromoCode.MembershipType.AARP);
        aarp.setValidFrom(LocalDate.of(2024, 1, 1));
        aarp.setValidUntil(LocalDate.of(2024, 12, 31));
        aarp.setIsActive(true);
        promoCodeRepository.save(aarp);

        // Military Discount - 20% off for service members and veterans
        PromoCode military = new PromoCode();
        military.setCode("MILITARY20");
        military.setDescription("Military & Veterans Discount - 20% off");
        military.setDiscountType(PromoCode.DiscountType.PERCENTAGE);
        military.setDiscountValue(new BigDecimal("20"));
        military.setMembershipType(PromoCode.MembershipType.MILITARY);
        military.setValidFrom(LocalDate.of(2024, 1, 1));
        military.setValidUntil(LocalDate.of(2024, 12, 31));
        military.setIsActive(true);
        promoCodeRepository.save(military);

        // Corporate Rate - Fixed $50 off with minimum 2-night stay
        PromoCode corporate = new PromoCode();
        corporate.setCode("CORP2024");
        corporate.setDescription("Corporate Rate - $50 off");
        corporate.setDiscountType(PromoCode.DiscountType.FIXED_AMOUNT);
        corporate.setDiscountValue(new BigDecimal("50"));
        corporate.setMembershipType(PromoCode.MembershipType.CORPORATE);
        corporate.setMinStayNights(2);  // Requires at least 2 nights
        corporate.setValidFrom(LocalDate.of(2024, 1, 1));
        corporate.setValidUntil(LocalDate.of(2024, 12, 31));
        corporate.setIsActive(true);
        promoCodeRepository.save(corporate);

        // Government Employee Rate - 12% off
        PromoCode government = new PromoCode();
        government.setCode("GOV2024");
        government.setDescription("Government Employee Rate - 12% off");
        government.setDiscountType(PromoCode.DiscountType.PERCENTAGE);
        government.setDiscountValue(new BigDecimal("12"));
        government.setMembershipType(PromoCode.MembershipType.GOVERNMENT);
        government.setValidFrom(LocalDate.of(2024, 1, 1));
        government.setValidUntil(LocalDate.of(2024, 12, 31));
        government.setIsActive(true);
        promoCodeRepository.save(government);

        // Early Bird Special - Limited to 100 uses, $75 off with 3+ night stay
        PromoCode earlyBird = new PromoCode();
        earlyBird.setCode("EARLYBIRD");
        earlyBird.setDescription("Early Bird Special - $75 off");
        earlyBird.setDiscountType(PromoCode.DiscountType.FIXED_AMOUNT);
        earlyBird.setDiscountValue(new BigDecimal("75"));
        earlyBird.setMembershipType(PromoCode.MembershipType.NONE);  // Anyone can use
        earlyBird.setMinStayNights(3);  // Minimum 3-night stay required
        earlyBird.setMaxUses(100);  // Limited to first 100 customers
        earlyBird.setValidFrom(LocalDate.now());
        earlyBird.setValidUntil(LocalDate.now().plusMonths(3));  // Valid for 3 months
        earlyBird.setIsActive(true);
        promoCodeRepository.save(earlyBird);

        System.out.println("✅ Seeded 6 sample promo codes (AAA, AARP, Military, Corporate, Government, Early Bird)");
    }
}