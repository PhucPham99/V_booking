package com.vbooking.backend.modules.inventory.repository;

import com.vbooking.backend.modules.inventory.entity.RoomRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRateRepository extends JpaRepository<RoomRateEntity, Long> {

    /**
     * Find room rates for a specific room type within a date range
     */
    List<RoomRateEntity> findByRoomTypeIdAndRateDateBetween(
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate);

    /**
     * Find available room rates (not closed) within date range
     */
    @Query("SELECT r FROM RoomRateEntity r WHERE r.roomTypeId = :roomTypeId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "AND r.isClosed = false")
    List<RoomRateEntity> findAvailableRates(
            @Param("roomTypeId") Long roomTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get all rates for a specific room type on specific dates
     */
    List<RoomRateEntity> findByRoomTypeIdAndRateDateIn(Long roomTypeId, List<LocalDate> dates);
}
