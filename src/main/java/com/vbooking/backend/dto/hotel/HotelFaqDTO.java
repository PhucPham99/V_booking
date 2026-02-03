package com.vbooking.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFaqDTO {
    private Long faqId;
    private String question;
    private String answer;
    private Integer displayOrder;
    private Boolean isActive;
}
