package com.vbooking.backend.modules.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_faqs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HotelFaqEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long faqId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "nvarchar(max)")
    private String answer;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}