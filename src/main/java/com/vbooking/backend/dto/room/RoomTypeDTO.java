package com.vbooking.backend.dto.room;

import com.vbooking.backend.dto.hotel.HotelPolicyDTO;
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
    private Long roomTypeId;
    private Long hotelId;
    private String name;
    private String description;
    private BigDecimal sizeM2;
    private Integer maxAdults;
    private Integer maxChildren;
    private String bedConfig;
    private List<String> amenities;
    private List<String> images;
    private BigDecimal defaultPrice;
    private Integer totalRooms;
    private Integer availableRooms;
    private HotelPolicyDTO policy;
    private Boolean isActive;
}
