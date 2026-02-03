package com.vbooking.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long hotelId;
    private String name;
    private String slug;
    private String description;
    private String address;
    private String city;
    private String district;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal starRating;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private String status;
    private List<String> amenities;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private String phone;
    private String email;
    private String website;

    private List<HotelImageDTO> images;
    private List<HotelFaqDTO> faqs;
    private List<HotelPolicyDTO> policies;
}
