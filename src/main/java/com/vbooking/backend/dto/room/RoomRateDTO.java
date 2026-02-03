package com.vbooking.backend.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateDTO {
    private Long rateId;
    private Long roomTypeId;
    private LocalDate rateDate;
    private BigDecimal price;
    private Integer availableCount;
    private Boolean isClosed;
}
