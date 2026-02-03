package com.vbooking.backend.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListDTO {
    private Long bookingId;
    private String bookingCode;
    private String hotelName;
    private String hotelImage;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal totalPayable;
    private String bookingStatus;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
