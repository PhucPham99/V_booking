package com.vbooking.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelImageDTO {
    private Long imageId;
    private String url;
    private String caption;
    private Boolean isPrimary;
    private Integer displayOrder;
}
