package com.vbooking.backend.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchRequest {
    private String keyword;
    private String city;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numAdults;
    private Integer numChildren;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<BigDecimal> starRating;
    private List<String> amenities;

    private String sortBy; // price_asc, price_desc, rating_desc

    private Integer page;
    private Integer size;
}
