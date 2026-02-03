package com.vbooking.backend.util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public class SecurityUtils {
	public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Logic này phụ thuộc vào cách bạn setup UserPrincipal trong SecurityConfig
        // Tạm thời trả về null hoặc throw ex nếu chưa đăng nhập
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        // Giả sử Principal là một object có method getId() (Bạn cần cast về UserPrincipal)
        // UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        // return principal.getId();
        
        return 1L; // Mock ID để test
    }
}
