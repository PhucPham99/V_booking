package com.vbooking.backend.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for guest booking (unauthenticated users)
 * Extends BookingRequest và thêm thông tin guest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookingRequest {

    // ========== BOOKING INFO ==========
    @NotNull(message = "Hotel ID không được để trống")
    private Long hotelId;

    @NotNull(message = "Room Type ID không được để trống")
    private Long roomTypeId;

    @NotNull(message = "Ngày check-in không được để trống")
    @FutureOrPresent(message = "Ngày check-in phải là hôm nay hoặc tương lai")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull(message = "Ngày check-out không được để trống")
    @Future(message = "Ngày check-out phải là ngày trong tương lai")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @Min(value = 1, message = "Số lượng phòng tối thiểu là 1")
    private Integer quantity;

    @Min(value = 1, message = "Số người lớn tối thiểu là 1")
    private Integer numAdults;

    private Integer numChildren;

    private String voucherCode;

    private String paymentMethod;

    // ========== GUEST INFO (Required for non-logged-in users) ==========

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String guestEmail;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
    private String guestPhone;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    private String guestName;

    /**
     * Cờ đánh dấu: Người dùng chấp nhận đặt phòng dưới dạng khách
     * dù email đã tồn tại (Soft Block Confirmation)
     */
    @Builder.Default
    private Boolean forceGuest = false;

    /**
     * Convert to standard BookingRequest (for service layer)
     */
    public BookingRequest toBookingRequest() {
        return BookingRequest.builder()
                .hotelId(this.hotelId)
                .roomTypeId(this.roomTypeId)
                .checkIn(this.checkIn)
                .checkOut(this.checkOut)
                .quantity(this.quantity)
                .numAdults(this.numAdults)
                .numChildren(this.numChildren)
                .voucherCode(this.voucherCode)

                .paymentMethod(this.paymentMethod)
                .build();
    }
}
