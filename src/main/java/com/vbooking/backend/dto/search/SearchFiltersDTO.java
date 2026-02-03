package com.vbooking.backend.dto.search;

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
public class SearchFiltersDTO {
    private List<String> cities;
    private List<BigDecimal> priceRange; // [min, max]
    private List<String> availableAmenities;
    private List<BigDecimal> starRatings;
    private List<String> reviewScores;
}
