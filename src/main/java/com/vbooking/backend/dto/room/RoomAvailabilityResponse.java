package com.vbooking.backend.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityResponse {
    private Boolean isAvailable;
    private Integer availableRooms;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal discount;
}
