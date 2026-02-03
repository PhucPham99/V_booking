package com.vbooking.backend.dto.booking;

import com.vbooking.backend.dto.hotel.HotelDTO;
import com.vbooking.backend.dto.promotion.PromotionDTO;
import com.vbooking.backend.dto.room.RoomTypeDTO;
import com.vbooking.backend.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long bookingId;
    private String bookingCode;
    private UserDTO user;
    private HotelDTO hotel;
    private RoomTypeDTO roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numNights;
    private Integer quantity;
    private Integer numAdults;
    private BigDecimal pricePerNight;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalPayable;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private List<PromotionDTO> promotions;
    private String cancellationReason;
}
