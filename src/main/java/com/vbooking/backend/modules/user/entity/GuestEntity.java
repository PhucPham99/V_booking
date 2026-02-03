package com.vbooking.backend.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "guests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GuestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "last_activity_at")
    @Builder.Default
    private LocalDateTime lastActivityAt = LocalDateTime.now();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}