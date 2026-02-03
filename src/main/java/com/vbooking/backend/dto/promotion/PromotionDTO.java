package com.vbooking.backend.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Long promotionId;
    private String name;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private Boolean isAutoApply;
    private Boolean isPublic;
    private BigDecimal minBookingAmount;
    private Integer minNights;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;
    private String status;
    private List<PromotionTargetDTO> targets;
}
