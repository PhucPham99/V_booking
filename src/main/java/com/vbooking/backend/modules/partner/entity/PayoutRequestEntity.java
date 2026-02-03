package com.vbooking.backend.modules.partner.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PayoutRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payout_id")
    private Long payoutId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "bank_name", columnDefinition = "nvarchar(100)")
    private String bankName;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_account_holder", columnDefinition = "nvarchar(100)")
    private String bankAccountHolder;

    @Column(name = "admin_note", columnDefinition = "nvarchar(max)")
    private String adminNote;

    @Column(name = "transaction_ref_code", length = 100)
    private String transactionRefCode;

    @CreatedDate
    @Column(name = "requested_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}