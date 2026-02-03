package com.vbooking.backend.dto.payout;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequestCreateRequest {

    @NotNull
    @DecimalMin("50000.00") // Example min payout
    private BigDecimal amount;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankAccountNumber;

    @NotBlank
    private String bankAccountHolder;
}
