package com.vbooking.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSessionDTO {

    private String tokenId;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private Boolean isActive;

    // Optional: Device info nếu muốn track
    // private String deviceInfo;
    // private String ipAddress;
}
