package com.vbooking.backend.dto.hotel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class HotelCreateRequest {

    @NotBlank
    @Size(min = 3, max = 150)
    private String name;

    private String description;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    private String district;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private BigDecimal starRating;

    private List<String> amenities;

    private String phone;

    @Email
    private String email;

    private String website;
}
