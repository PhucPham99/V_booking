package com.vbooking.backend.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionDTO {
    private String transactionId; // Using UUID for transactions if applicable, or logic ID
    private Long walletId;
    private BigDecimal amount;
    private String type;
    private String referenceId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
