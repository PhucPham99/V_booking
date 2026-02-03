package com.vbooking.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchDTO {
    private Long hotelId;
    private String name;
    private BigDecimal starRating;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private String city;
    private String address;
    private BigDecimal startingPrice;
    private String thumbnailUrl;
    private List<String> amenities;
}
