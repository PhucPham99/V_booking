package com.vbooking.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;

    private List<String> images;
}
