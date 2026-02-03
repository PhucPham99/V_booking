package com.vbooking.backend.modules.booking.repository;

import com.vbooking.backend.modules.booking.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByBookingCode(String bookingCode);

    Page<BookingEntity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT b FROM BookingEntity b WHERE b.hotelId = :hotelId")
    Page<BookingEntity> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    // Check if user has stayed at a hotel (for review eligibility)
    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b " +
            "WHERE b.userId = :userId " +
            "AND b.hotelId = :hotelId " +
            "AND b.bookingStatus = 'completed'") // Updated status field name to match Entity
    boolean hasCompletedBooking(@Param("userId") Long userId, @Param("hotelId") Long hotelId);
}
