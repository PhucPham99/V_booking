package com.vbooking.backend.modules.partner.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_code", nullable = false, length = 50)
    private String taxCode;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "pending_kyc";

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