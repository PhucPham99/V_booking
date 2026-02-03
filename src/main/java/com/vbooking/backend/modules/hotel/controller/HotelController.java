package com.vbooking.backend.modules.hotel.controller;

import com.vbooking.backend.dto.common.ApiResponse;
import com.vbooking.backend.dto.common.PageableResponse;
import com.vbooking.backend.dto.hotel.HotelDTO;
import com.vbooking.backend.dto.search.HotelSearchRequest;
import com.vbooking.backend.modules.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ApiResponse<PageableResponse<HotelDTO>> searchHotels(@ModelAttribute HotelSearchRequest request) {
        PageableResponse<HotelDTO> response = hotelService.searchHotels(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<HotelDTO> getHotelDetail(@PathVariable Long id) {
        HotelDTO hotel = hotelService.getHotelDetail(id);
        return ApiResponse.success(hotel);
    }

    @GetMapping("/slug/{slug}")
    public ApiResponse<HotelDTO> getHotelDetailBySlug(@PathVariable String slug) {
        HotelDTO hotel = hotelService.getHotelDetailBySlug(slug);
        return ApiResponse.success(hotel);
    }

    @GetMapping("/{id}/rooms")
    public ApiResponse<java.util.List<com.vbooking.backend.dto.hotel.RoomTypeDTO>> getHotelRooms(
            @PathVariable Long id) {
        return ApiResponse.success(hotelService.getHotelRoomTypes(id));
    }
}
