package com.vbooking.backend.modules.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_code", unique = true, nullable = false, length = 12)
    private String bookingCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "num_nights")
    private Integer numNights;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "num_adults")
    private Integer numAdults;

    @Column(name = "guest_count")
    private Integer guestCount;

    @Column(name = "price_per_night")
    private BigDecimal pricePerNight;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "total_discount")
    private BigDecimal totalDiscount;

    @Column(name = "total_payable")
    private BigDecimal totalPayable;

    @Column(name = "commission_amount")
    private BigDecimal commissionAmount;

    @Column(name = "hotel_payout")
    private BigDecimal hotelPayout;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Column(name = "booking_status")
    private String bookingStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_response", columnDefinition = "nvarchar(max)")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancellation_reason", columnDefinition = "nvarchar(max)")
    private String cancellationReason;

    // Soft delete (V4 renamed)
    @Column(name = "is_cancel")
    @Builder.Default
    private Boolean isCancel = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Timestamps
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}