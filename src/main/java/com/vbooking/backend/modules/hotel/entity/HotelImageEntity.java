package com.vbooking.backend.modules.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "caption")
    private String caption;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
