package com.vbooking.backend.modules.inventory.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hotel_room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RoomTypeEntity { // Plain entity - matches DB schema exactly

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "size_m2")
    private BigDecimal sizeM2;

    @Column(name = "max_adults")
    @Builder.Default
    private Integer maxAdults = 2;

    @Column(name = "max_children")
    @Builder.Default
    private Integer maxChildren = 0;

    @Column(name = "bed_config", length = 100)
    private String bedConfig;

    @Type(JsonType.class)
    @Column(name = "amenities", columnDefinition = "nvarchar(max)")
    private List<String> amenities;

    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "nvarchar(max)")
    private List<String> images;

    @Column(name = "default_price", nullable = false)
    private BigDecimal defaultPrice;

    @Column(name = "total_rooms")
    @Builder.Default
    private Integer totalRooms = 1;

    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Soft delete columns (from V4 migration)
    @Column(name = "is_cancel")
    @Builder.Default
    private Boolean isCancel = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Timestamp column
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}