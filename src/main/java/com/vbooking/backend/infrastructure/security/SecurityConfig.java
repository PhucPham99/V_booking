package com.vbooking.backend.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Cho phép dùng @PreAuthorize("hasRole('ADMIN')") tại Controller
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    // Bean mã hóa mật khẩu (BCrypt)
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean quản lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Cấu hình chính của Security Filter Chain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Vì dùng JWT Stateless nên không cần chống CSRF như Session)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Cấu hình CORS (Cho phép Frontend gọi API)
                .cors(Customizer.withDefaults())

                // 3. Phân quyền truy cập URL (Authorize Requests)
                .authorizeHttpRequests((authorize) -> authorize

                        // --- NHÓM 1: PUBLIC (Ai cũng vào được) ---

                        // Auth: Login, Register, Forgot Password
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Hotel & Room: Chỉ cho xem (GET), không cho sửa/xóa
                        .requestMatchers(HttpMethod.GET, "/api/v1/hotels/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/room-types/**").permitAll()

                        // Review & Promotion: Khách xem đánh giá và khuyến mãi công khai
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/promotions/public/**").permitAll()

                        // --- NHÓM 2: BOOKING FLOW (Cho phép đi đến bước thanh toán) ---

                        // Kiểm tra phòng trống (Khách check ngày)
                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/check-availability").permitAll()

                        // Guest Booking (Đặt phòng không cần đăng nhập)
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings/guest").permitAll()

                        // Tính toán giá tiền (Màn hình Review Booking trước khi thanh toán)
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings/preview").permitAll()

                        // Giữ phòng tạm (Nếu bạn dùng tính năng giữ chỗ 10p)
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings/hold").permitAll()

                        // --- NHÓM 3: PRIVATE (Bắt buộc Login) ---

                        // Tạo Booking thật (Lưu xuống DB) -> BẮT BUỘC AUTHENTICATED
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings").authenticated()

                        // Thanh toán, User Profile, Admin -> BẮT BUỘC AUTHENTICATED
                        .requestMatchers("/api/v1/payment/**").authenticated()
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Hoặc cấu hình ở Controller

                        // Các request còn lại chưa định nghĩa -> Chặn hết
                        .anyRequest().authenticated())

                // 4. Xử lý lỗi khi chưa đăng nhập (Trả về JSON 401 thay vì trang HTML)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))

                // 5. Quản lý Session: STATELESS (Không lưu session trên server)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 6. Thêm Filter JWT vào trước Filter xác thực mặc định
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean cấu hình CORS
    @Bean
    org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        // ALLOW ALL FOR DEV
        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("*"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}