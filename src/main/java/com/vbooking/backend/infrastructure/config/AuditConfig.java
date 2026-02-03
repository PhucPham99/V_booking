package com.vbooking.backend.infrastructure.config;

import com.vbooking.backend.infrastructure.common.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        // Tự động lấy User ID từ Security Context để điền vào created_by/updated_by
        return () -> {
            try {
                Long userId = SecurityUtils.getCurrentUserId();
                return Optional.ofNullable(userId != null ? userId.toString() : "SYSTEM");
            } catch (Exception e) {
                return Optional.of("ANONYMOUS"); // Trường hợp chưa đăng nhập (như Register)
            }
        };
    }
}