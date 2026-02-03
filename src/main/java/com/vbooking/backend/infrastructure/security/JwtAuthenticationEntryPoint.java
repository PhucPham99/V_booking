package com.vbooking.backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbooking.backend.infrastructure.common.models.ApiResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Auto-generate constructor for final fields
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // Inject Spring's ObjectMapper

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized: Bạn cần đăng nhập để truy cập tài nguyên này.");

        // Ghi JSON ra response bằng ObjectMapper đã inject (có module JSR310)
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}