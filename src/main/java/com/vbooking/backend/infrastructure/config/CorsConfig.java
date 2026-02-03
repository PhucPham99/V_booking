package com.vbooking.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 1. Cho phép các nguồn (Frontend) được gọi API
        // Nếu bạn chạy local thì để localhost:3000 (React) hoặc localhost:4200 (Angular)
        // Khi deploy lên server thật, hãy thay bằng domain thật (ví dụ: https://vbooking.com)
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173")); 
        
        // Hoặc dùng "*" để cho phép tất cả (Không khuyến khích khi có allowCredentials=true)
        // config.addAllowedOriginPattern("*");

        // 2. Cho phép các Header và Method
        config.addAllowedHeader("*"); // Cho phép mọi header (Authorization, Content-Type...)
        config.addAllowedMethod("*"); // Cho phép mọi method (GET, POST, PUT, DELETE, OPTIONS)

        // 3. Cho phép gửi Cookie/Token kèm theo (Quan trọng nếu dùng Session hoặc Cookie)
        config.setAllowCredentials(true);

        // 4. Áp dụng cấu hình này cho mọi đường dẫn API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}