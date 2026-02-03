package com.vbooking.backend.dto.auth;

import com.vbooking.backend.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String role;
    private UserDTO user;
}
