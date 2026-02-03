package com.vbooking.backend.dto.payout;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutApprovalRequest {

    @NotNull
    private Long payoutId;

    @NotNull
    private Boolean approved;

    private String adminNote;

    private String transactionRefCode;
}
