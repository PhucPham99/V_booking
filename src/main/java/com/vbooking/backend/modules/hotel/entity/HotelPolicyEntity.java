package com.vbooking.backend.modules.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_policies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HotelPolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "policy_type", length = 50)
    @Builder.Default
    private String policyType = "cancellation";

    @Column(name = "free_cancellation_before_hours")
    @Builder.Default
    private Integer freeCancellationBeforeHours = 24;

    @Column(name = "cancellation_fee_percent")
    @Builder.Default
    private BigDecimal cancellationFeePercent = BigDecimal.ZERO;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}