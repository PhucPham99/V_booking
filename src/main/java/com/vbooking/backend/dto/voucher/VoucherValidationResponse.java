package com.vbooking.backend.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidationResponse {
    private Boolean isValid;
    private BigDecimal discountAmount;
    private String message;
}
