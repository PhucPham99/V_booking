package com.vbooking.backend.dto.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmPaymentRequest {

    @NotBlank
    private String bookingCode;

    @NotBlank
    private String transactionId;

    @NotBlank
    private String paymentMethod;

    private String gatewayResponse;
}
