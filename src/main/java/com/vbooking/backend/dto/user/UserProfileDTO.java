package com.vbooking.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long userId;
    private String email;
    private String phone;
    private String fullName;
    private LocalDate dateOfBirth;
    private String addressInfo; // JSON string
    private String avatarUrl;
    private String role;
    private LocalDateTime createdAt;
}
