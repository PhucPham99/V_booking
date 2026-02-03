package com.vbooking.backend.modules.partner.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "partner_id", nullable = false, unique = true)
    private Long partnerId;

    @Column(name = "balance")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "VND";

    @Column(name = "amount_on_hold")
    @Builder.Default
    private BigDecimal amountOnHold = BigDecimal.ZERO;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}