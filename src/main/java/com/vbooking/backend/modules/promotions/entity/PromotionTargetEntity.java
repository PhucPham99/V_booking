package com.vbooking.backend.modules.promotions.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion_targets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PromotionTargetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // hotel, room_type

    @Column(name = "target_entity_id", nullable = false)
    private Long targetEntityId;
}