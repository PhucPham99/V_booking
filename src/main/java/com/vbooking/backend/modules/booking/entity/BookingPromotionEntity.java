package com.vbooking.backend.modules.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_promotions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookingPromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_promo_id")
    private Long bookingPromoId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
}