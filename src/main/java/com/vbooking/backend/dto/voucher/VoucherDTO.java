package com.vbooking.backend.dto.voucher;

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
public class VoucherDTO {
    private Long voucherId;
    private String code;
    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minBookingAmount;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
