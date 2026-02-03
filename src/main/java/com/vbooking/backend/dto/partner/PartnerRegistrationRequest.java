package com.vbooking.backend.dto.partner;

import com.vbooking.backend.dto.auth.RegisterRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRegistrationRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String taxCode;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    private String address;

    @NotNull
    private RegisterRequest userInfo;
}
