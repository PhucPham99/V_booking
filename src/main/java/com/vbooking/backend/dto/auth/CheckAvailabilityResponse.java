package com.vbooking.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for email/phone check endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponse {

    private boolean exists;
    private String message;
}
