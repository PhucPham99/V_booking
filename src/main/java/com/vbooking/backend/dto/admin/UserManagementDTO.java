package com.vbooking.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDTO {
    private Long userId;

    private String email;
    private String role;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<String> activity;
}
