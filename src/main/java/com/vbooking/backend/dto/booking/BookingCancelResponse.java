package com.vbooking.backend.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelResponse {
    private Long bookingId;
    private String status;
    private BigDecimal cancellationFee;
    private BigDecimal refundAmount;
}
