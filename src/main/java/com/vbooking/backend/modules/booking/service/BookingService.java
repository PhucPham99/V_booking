package com.vbooking.backend.modules.booking.service;

import com.vbooking.backend.dto.booking.BookingDTO;
import com.vbooking.backend.dto.booking.BookingRequest;
import com.vbooking.backend.dto.booking.BookingListDTO;
import com.vbooking.backend.modules.booking.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDTO createBooking(Long userId, BookingRequest request);

    BookingDTO getBookingByCode(String bookingCode);

    Page<BookingListDTO> getUserBookings(Long userId, Pageable pageable);

    void cancelBooking(Long userId, Long bookingId, String reason);

    // Admin/Partner methods
    BookingDTO updateStatus(Long bookingId, String status);
}
