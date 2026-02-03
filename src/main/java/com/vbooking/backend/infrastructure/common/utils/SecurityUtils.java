package com.vbooking.backend.infrastructure.common.utils;
import com.vbooking.backend.infrastructure.security.UserPrincipal;
import lombok.experimental.UtilityClass; // Lombok giúp class này thành Utility (final, private constructor)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.stream.Stream;
@UtilityClass // Annotation này của Lombok, tự động biến class thành final và constructor private
public class SecurityUtils {
	/**
     * Lấy ID của user đang đăng nhập hiện tại.
     * @return Long userId hoặc ném ra Exception nếu chưa đăng nhập
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || isAnonymous(authentication)) {
            // Tùy chọn: Return null hoặc throw Exception. 
            // Với hệ thống API, throw Exception để GlobalExceptionHandler bắt lỗi 401 là tốt nhất.
            throw new RuntimeException("Người dùng chưa xác thực (Unauthorized)"); 
            // Bạn có thể thay bằng BusinessException("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        Object principal = authentication.getPrincipal();
        
        // UserPrincipal là class custom của bạn implements UserDetails
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        return null;
    }

    /**
     * Lấy Username (hoặc Email) của user hiện tại.
     */
    public static Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUsername());
        } else if (principal instanceof String principalString) {
            return Optional.of(principalString);
        }

        return Optional.empty();
    }

    /**
     * Kiểm tra user hiện tại có đang đăng nhập hay không?
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !isAnonymous(authentication);
    }

    /**
     * Kiểm tra user hiện tại có quyền (Role) cụ thể không?
     * Ví dụ: SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")
     */
    public static boolean hasCurrentUserThisAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication)
                .anyMatch(authority::equals);
    }

    // --- Helper Methods ---

    private static boolean isAnonymous(Authentication authentication) {
        return "anonymousUser".equals(authentication.getPrincipal());
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority);
    }
}
