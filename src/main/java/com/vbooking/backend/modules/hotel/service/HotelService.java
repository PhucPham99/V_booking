package com.vbooking.backend.modules.hotel.service;

import com.vbooking.backend.dto.common.PageableResponse;
import com.vbooking.backend.dto.hotel.HotelDTO;
import com.vbooking.backend.dto.search.HotelSearchRequest;

public interface HotelService {
    PageableResponse<HotelDTO> searchHotels(HotelSearchRequest request);

    HotelDTO getHotelDetail(Long id);

    HotelDTO getHotelDetailBySlug(String slug);

    // Valid for MVP: Fetch rooms for booking
    java.util.List<com.vbooking.backend.dto.hotel.RoomTypeDTO> getHotelRoomTypes(Long hotelId);
}
