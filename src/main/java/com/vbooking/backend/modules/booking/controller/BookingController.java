package com.vbooking.backend.modules.booking.controller;

import com.vbooking.backend.dto.common.ApiResponse;
import com.vbooking.backend.dto.common.PageableResponse;
import com.vbooking.backend.dto.booking.BookingDTO;
import com.vbooking.backend.dto.booking.BookingListDTO;
import com.vbooking.backend.dto.booking.BookingRequest;
import com.vbooking.backend.dto.booking.GuestBookingRequest;
import com.vbooking.backend.dto.booking.BookingCancelRequest;
import com.vbooking.backend.infrastructure.security.UserPrincipal;
import com.vbooking.backend.modules.booking.service.BookingService;
import com.vbooking.backend.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    // 0. Create Booking for Guests (Hybrid Flow - Shadow User)
    /**
     * Endpoint for guest (non-logged-in) booking
     * POST /api/v1/bookings/guest
     * 
     * Flow:
     * 1. Check if email already exists in users table
     * 2. If exists → use existing userId
     * 3. If NOT exists → create shadow user (isActive=false, passwordHash=null)
     * 4. Create booking with userId
     * 5. Send combined email: booking confirmation + account activation link
     */
    @PostMapping("/guest")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingDTO> createGuestBooking(@Valid @RequestBody GuestBookingRequest request) {
        log.info("Guest booking request received for email: {}", request.getGuestEmail());

        // 0. SOFT BLOCK CHECK: Nếu email đã tồn tại và chưa xác nhận tiếp tục
        // (forceGuest=false)
        boolean isEmailRegistered = userService.isEmailRegistered(request.getGuestEmail());
        if (isEmailRegistered && !Boolean.TRUE.equals(request.getForceGuest())) {
            // Trả về 409 để Frontend hiện Popup "Bạn đã có tài khoản..."
            throw new com.vbooking.backend.infrastructure.exception.AppException(
                    HttpStatus.CONFLICT, "EMAIL_EXISTS_PLEASE_LOGIN");
        }

        // 1. Get or create user
        Long userId = userService.getUserOrCreateShadow(
                request.getGuestEmail(),
                request.getGuestPhone(),
                request.getGuestName(),
                true // Create shadow user if not exists
        );

        log.info("Using userId: {} for guest booking", userId);

        // 2. Create booking
        BookingRequest bookingRequest = request.toBookingRequest();
        BookingDTO booking = bookingService.createBooking(userId, bookingRequest);

        log.info("Booking created successfully with code: {}", booking.getBookingCode());

        // 3. Send email & Determine response message
        String responseMessage;

        if (isEmailRegistered) {
            // Case: User cũ chọn "Tiếp tục đặt" (Force Guest)
            // Gửi email confirm (không link active)
            userService.sendBookingConfirmation(userId, booking.getBookingCode());
            responseMessage = "Đặt phòng thành công! Email xác nhận đã được gửi. Lưu ý: Bạn đã có tài khoản, hãy đăng nhập lần sau để nhận ưu đãi.";
        } else {
            // Case: User mới tinh
            // Gửi email activation
            log.info("========================================");
            log.info("DEBUG: Sending email for NEW user");
            log.info("User ID: {}", userId);
            log.info("Email: {}", request.getGuestEmail());
            log.info("Booking Code: {}", booking.getBookingCode());
            log.info("========================================");

            try {
                userService.sendAccountActivationEmail(userId, booking.getBookingCode());
                log.info("✅ SUCCESS: Email sent successfully to: {}", request.getGuestEmail());
            } catch (Exception e) {
                log.error("❌ FAILED: Error sending email to: {}", request.getGuestEmail(), e);
            }
            responseMessage = "Đặt phòng thành công! Vui lòng kiểm tra email để nhận thông tin đăng nhập.";
        }

        return ApiResponse.success(booking, responseMessage);
    }

    // 1. Create Booking (For Logged-In Users)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingDTO> createBooking(@AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody BookingRequest request) {
        BookingDTO booking = bookingService.createBooking(currentUser.getId(), request);
        return ApiResponse.success(booking, "Booking created successfully");
    }

    // 2. Get Booking Details (By Code)
    @GetMapping("/{bookingCode}")
    public ApiResponse<BookingDTO> getBookingByCode(@PathVariable String bookingCode) {
        BookingDTO booking = bookingService.getBookingByCode(bookingCode);
        return ApiResponse.success(booking);
    }

    // 3. Get My Bookings
    @GetMapping("/my-bookings")
    public ApiResponse<PageableResponse<BookingListDTO>> getMyBookings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookingListDTO> bookingPage = bookingService.getUserBookings(currentUser.getId(), pageable);

        PageableResponse<BookingListDTO> response = new PageableResponse<>(
                bookingPage.getContent(),
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isLast(),
                bookingPage.isFirst());

        return ApiResponse.success(response);
    }

    // 4. Cancel Booking
    @PostMapping("/{bookingId}/cancel")
    public ApiResponse<String> cancelBooking(@AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long bookingId,
            @RequestBody(required = false) BookingCancelRequest request) {
        String reason = (request != null) ? request.getReason() : "No reason provided";
        bookingService.cancelBooking(currentUser.getId(), bookingId, reason);
        return ApiResponse.success("Booking cancelled successfully");
    }
}
