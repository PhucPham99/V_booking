package com.vbooking.backend.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelApprovalRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Boolean approved;

    private String rejectionReason;
}
