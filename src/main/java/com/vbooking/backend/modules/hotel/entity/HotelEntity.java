package com.vbooking.backend.modules.hotel.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", unique = true, length = 160)
    private String slug;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "star_rating")
    @Builder.Default
    private BigDecimal starRating = BigDecimal.ZERO;

    @Column(name = "commission_rate")
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("15.00");

    @Column(name = "checkin_time")
    @Builder.Default
    private LocalTime checkinTime = LocalTime.of(14, 0);

    @Column(name = "checkout_time")
    @Builder.Default
    private LocalTime checkoutTime = LocalTime.of(12, 0);

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "website")
    private String website;

    @Type(JsonType.class)
    @Column(name = "amenities", columnDefinition = "nvarchar(max)")
    private List<String> amenities;

    @Column(name = "average_rating")
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "rejection_reason", columnDefinition = "nvarchar(max)")
    private String rejectionReason;

    // Soft delete (V4 renamed)
    @Column(name = "is_cancel")
    @Builder.Default
    private Boolean isCancel = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Timestamps
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
