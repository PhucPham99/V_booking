package com.vbooking.backend.dto.room;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityRequest {

    @NotNull
    private Long roomTypeId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @NotNull
    private Integer requiredQuantity;
}
