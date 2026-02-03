package com.vbooking.backend.modules.booking.service.impl;

import com.vbooking.backend.dto.booking.BookingDTO;
import com.vbooking.backend.dto.booking.BookingListDTO;
import com.vbooking.backend.dto.booking.BookingRequest;
import com.vbooking.backend.infrastructure.exception.AppException;
import com.vbooking.backend.infrastructure.exception.ResourceNotFoundException;
import com.vbooking.backend.modules.booking.entity.BookingEntity;
import com.vbooking.backend.modules.booking.repository.BookingRepository;
import com.vbooking.backend.modules.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.vbooking.backend.modules.auth.repository.RefreshTokenRepository; // Add import

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final com.vbooking.backend.modules.promotions.repository.VoucherRepository voucherRepository;

    // In a real implementation, you would inject other Repositories/Services here:
    // private final RoomTypeRepository roomTypeRepository;
    // private final HotelRepository hotelRepository;
    // private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDTO createBooking(Long userId, BookingRequest request) {
        // 1. Validate Room Availability (Mock logic for now)
        // RoomTypeEntity roomType =
        // roomTypeRepository.findById(request.getRoomTypeId())...

        // 2. Calculate Prices
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights < 1)
            nights = 1;

        BigDecimal pricePerNight = BigDecimal.valueOf(500000); // Mock price
        BigDecimal subtotal = pricePerNight.multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 2.1 Calculate Discount (System Voucher Logic)
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Check First-Time Login Voucher (Count refresh tokens <= 1)
        long tokenCount = refreshTokenRepository.countByUserId(userId);
        if (tokenCount <= 1) {
            // Fetch System Voucher from DB
            String voucherCode = "WELCOME100";
            java.util.Optional<com.vbooking.backend.modules.promotions.entity.VoucherEntity> voucherOpt = voucherRepository
                    .findByCode(voucherCode);

            if (voucherOpt.isPresent()) {
                BigDecimal discountValue = voucherOpt.get().getDiscountValue();
                log.info("Applying System Voucher {} (Value: {}) for first-time user: {}",
                        voucherCode, discountValue, userId);

                // Set discount (Fixed Amount)
                totalDiscount = discountValue;
            } else {
                log.warn("System Voucher {} not found in database! fallback to 0 discount.", voucherCode);
            }
        }

        // Ensure discount doesn't exceed subtotal
        if (totalDiscount.compareTo(subtotal) > 0) {
            totalDiscount = subtotal;
        }

        BigDecimal totalPayable = subtotal.subtract(totalDiscount);

        // 3. Create Entity
        BookingEntity booking = BookingEntity.builder()
                .bookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .userId(userId)
                .hotelId(request.getHotelId())
                .roomTypeId(request.getRoomTypeId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .numNights((int) nights)
                .quantity(request.getQuantity())
                .numAdults(request.getNumAdults())

                .pricePerNight(pricePerNight)
                .subtotal(subtotal)
                .totalDiscount(totalDiscount)
                .totalPayable(totalPayable)
                .commissionAmount(totalPayable.multiply(BigDecimal.valueOf(0.15))) // 15% commission
                .hotelPayout(totalPayable.multiply(BigDecimal.valueOf(0.85)))

                .bookingStatus("pending_payment")
                .paymentStatus("pending")
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "offline")

                .build();

        BookingEntity savedBooking = bookingRepository.save(booking);

        return mapToDTO(savedBooking);
    }

    @Override
    public BookingDTO getBookingByCode(String bookingCode) {
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));
        return mapToDTO(booking);
    }

    @Override
    public Page<BookingListDTO> getUserBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
                .map(this::mapToListDTO);
    }

    @Override
    @Transactional
    public void cancelBooking(Long userId, Long bookingId, String reason) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not allowed to cancel this booking");
        }

        if (!"pending_payment".equals(booking.getBookingStatus()) && !"confirmed".equals(booking.getBookingStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel booking in status: " + booking.getBookingStatus());
        }

        booking.setBookingStatus("cancelled");
        booking.setCancellationReason(reason);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingDTO updateStatus(Long bookingId, String status) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        booking.setBookingStatus(status);
        return mapToDTO(bookingRepository.save(booking));
    }

    // --- MAPPERS ---

    private BookingDTO mapToDTO(BookingEntity entity) {
        return BookingDTO.builder()
                .bookingId(entity.getBookingId())
                .bookingCode(entity.getBookingCode())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .numNights(entity.getNumNights())
                .quantity(entity.getQuantity())
                .numAdults(entity.getNumAdults())
                .pricePerNight(entity.getPricePerNight())
                .subtotal(entity.getSubtotal())
                .totalDiscount(entity.getTotalDiscount())
                .totalPayable(entity.getTotalPayable())
                .bookingStatus(entity.getBookingStatus())
                .paymentStatus(entity.getPaymentStatus())
                .createdAt(entity.getCreatedAt())
                // .user(fetchUserDTO(entity.getUserId())) // In real app, fetch these
                // .hotel(fetchHotelDTO(entity.getHotelId()))
                .build();
    }

    private BookingListDTO mapToListDTO(BookingEntity entity) {
        return BookingListDTO.builder()
                .bookingId(entity.getBookingId())
                .bookingCode(entity.getBookingCode())
                // .hotelName(...)
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .totalPayable(entity.getTotalPayable())
                .bookingStatus(entity.getBookingStatus())
                .paymentStatus(entity.getPaymentStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
