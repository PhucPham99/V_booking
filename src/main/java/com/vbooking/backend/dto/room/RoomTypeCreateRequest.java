package com.vbooking.backend.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RoomTypeCreateRequest {

    @NotBlank
    private String name;

    private String description;

    private BigDecimal sizeM2;

    private Integer maxAdults;

    private Integer maxChildren;

    private String bedConfig;

    private List<String> amenities;

    @NotNull
    private BigDecimal defaultPrice;

    private Integer totalRooms;

    private Long policyId;
}
