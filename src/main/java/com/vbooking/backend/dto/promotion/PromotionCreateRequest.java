package com.vbooking.backend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PromotionCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String discountType;

    @NotNull
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    private Boolean isAutoApply;

    private BigDecimal minBookingAmount;

    private Integer minNights;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookingStartDate;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookingEndDate;

    private String status;

    private String targetType;

    private List<Long> targetEntityIds;
}
