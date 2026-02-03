package com.vbooking.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelPolicyDTO {
    private Long policyId;
    private String name;
    private String description;
    private String policyType;
    private Integer freeCancellationBeforeHours;
    private BigDecimal cancellationFeePercent;
    private Boolean isDefault;
}
