package com.vbooking.backend.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
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
public class BookingRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long roomTypeId;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @Min(1)
    private Integer quantity;

    @Min(1)
    private Integer numAdults;

    private Integer numChildren;

    private String voucherCode;

    private String paymentMethod;
}
