package com.vbooking.backend.modules.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// Bảng này không cần extends BaseEntity vì nó thuần túy là data point, không cần audit
@Entity
@Table(name = "hotel_room_rates")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // Dùng Builder thường vì không extends BaseEntity
public class RoomRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "available_count", nullable = false)
    @Builder.Default
    private Integer availableCount = 0;

    @Column(name = "is_closed")
    @Builder.Default
    private Boolean isClosed = false;
}