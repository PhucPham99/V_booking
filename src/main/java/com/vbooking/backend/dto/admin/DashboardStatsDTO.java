package com.vbooking.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private BigDecimal revenueChange;
    private Long totalBookings;
    private BigDecimal bookingsChange;
    private BigDecimal totalCommission;
    private BigDecimal commissionChange;
    private BigDecimal pendingPayouts;
    private BigDecimal pendingPayoutsChange;
}
