package com.vbooking.backend.modules.hotel.repository;

import com.vbooking.backend.modules.hotel.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long>, JpaSpecificationExecutor<HotelEntity> {

    Optional<HotelEntity> findBySlug(String slug);

    @Query("SELECT h FROM HotelEntity h WHERE h.status = 'active' AND h.isCancel = false")
    List<HotelEntity> findAllActiveHotels();

    // Add more custom queries if needed, but SpecificationExecutor should handle
    // most search cases
}
