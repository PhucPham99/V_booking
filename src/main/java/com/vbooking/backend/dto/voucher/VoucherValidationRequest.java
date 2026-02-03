package com.vbooking.backend.dto.voucher;

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
public class VoucherValidationRequest {

    @NotBlank
    private String code;

    @NotNull
    private BigDecimal bookingAmount;
}
