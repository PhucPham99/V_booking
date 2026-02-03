package com.vbooking.backend.modules.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_holds")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomHoldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hold_id")
    private Long holdId;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "num_rooms")
    @Builder.Default
    private Integer numRooms = 1;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}