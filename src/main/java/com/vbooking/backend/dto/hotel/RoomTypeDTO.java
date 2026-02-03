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
public class RoomTypeDTO {
    private Long id;
    private String name;
    private String nameEn;
    private String size;
    private Integer maxAdults;
    private Integer maxChildren;
    private String beds;
    private BigDecimal pricePerNight;
    private Integer available;
    private List<String> amenities;
    private List<String> features; // icons, etc.
    private String imageUrl; // simple string for now
}
