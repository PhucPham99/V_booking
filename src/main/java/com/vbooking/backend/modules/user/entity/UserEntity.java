package com.vbooking.backend.modules.user.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "full_name", columnDefinition = "nvarchar(100)")
    private String fullName;

    @Column(name = "provider", length = 20)
    @Builder.Default
    private String provider = "local";

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "role", length = 20)
    @Builder.Default
    private String role = "guest";

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Type(JsonType.class)
    @Column(name = "address_info", columnDefinition = "nvarchar(max)")
    private AddressInfo addressInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfo implements java.io.Serializable {
        private String street;
        private String district;
        private String city;
        private String country;
        private String postalCode;
    }

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    // Soft delete (V4 renamed from is_deleted -> is_cancel, deleted_at ->
    // cancelled_at)
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
