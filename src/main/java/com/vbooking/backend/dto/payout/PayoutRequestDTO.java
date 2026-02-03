package com.vbooking.backend.dto.payout;

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
public class PayoutRequestDTO {
    private Long payoutId;
    private Long partnerId;
    private BigDecimal amount;
    private String status;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String adminNote;
    private String transactionRefCode;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
