package com.vbooking.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;

    private String email;
    private String phone;
    private String fullName;
    private String role;
    private String avatarUrl;
    private String status;
    private LocalDateTime createdAt;
    private AddressDTO addressInfo;
}
