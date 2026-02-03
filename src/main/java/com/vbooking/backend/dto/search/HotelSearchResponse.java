package com.vbooking.backend.dto.search;

import com.vbooking.backend.dto.hotel.HotelSearchDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchResponse {
    private List<HotelSearchDTO> hotels;
    private long totalResults;
    private int page;
    private int totalPages;
    private SearchFiltersDTO filters;
}
