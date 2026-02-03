package com.vbooking.backend.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelRequest {

    @NotNull
    private Long bookingId;

    private String reason;
}
