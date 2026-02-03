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
public class WalletDTO {
    private Long walletId;
    private Long partnerId;
    private BigDecimal balance;
    private String currency;
    private BigDecimal amountOnHold;
    private LocalDateTime lastUpdated;
}
