package com.vbooking.backend.modules.promotions.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PromotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "is_auto_apply")
    @Builder.Default
    private Boolean isAutoApply = true;

    @Column(name = "booking_start_date", nullable = false)
    private LocalDateTime bookingStartDate;

    @Column(name = "booking_end_date", nullable = false)
    private LocalDateTime bookingEndDate;

    @Column(name = "travel_start_date")
    private LocalDateTime travelStartDate;

    @Column(name = "travel_end_date")
    private LocalDateTime travelEndDate;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "active";

    // Timestamp (created_at only per migration)
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}