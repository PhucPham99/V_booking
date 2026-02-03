package com.vbooking.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed response for email availability check
 * Used to show smart login suggestions (Booking.com style)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckDetailedResponse {

    /**
     * Email đã tồn tại trong hệ thống
     */
    private boolean exists;

    /**
     * User đã kích hoạt tài khoản (isActive=true)
     */
    private Boolean isActive;

    /**
     * User đã có mật khẩu
     */
    private Boolean hasPassword;

    /**
     * Message gợi ý cho frontend hiển thị
     * 
     * Examples:
     * - "Email khả dụng"
     * - "Bạn đã có tài khoản. Đăng nhập để nhận ưu đãi thành viên?"
     * - "Email này đã được dùng từ booking trước"
     */
    private String suggestion;

    /**
     * Loại user: member, shadow, new
     */
    private UserType userType;

    public enum UserType {
        NEW, // Email chưa tồn tại
        ACTIVE_MEMBER, // isActive=true, có password
        SHADOW_USER // isActive=false hoặc không có password
    }

    /**
     * Factory method cho email mới
     */
    public static EmailCheckDetailedResponse newEmail() {
        return EmailCheckDetailedResponse.builder()
                .exists(false)
                .userType(UserType.NEW)
                .suggestion("Email khả dụng")
                .build();
    }

    /**
     * Factory method cho active member
     */
    public static EmailCheckDetailedResponse activeMember() {
        return EmailCheckDetailedResponse.builder()
                .exists(true)
                .isActive(true)
                .hasPassword(true)
                .userType(UserType.ACTIVE_MEMBER)
                .suggestion("Bạn đã có tài khoản VBOOKING. Đăng nhập để nhận ưu đãi thành viên?")
                .build();
    }

    /**
     * Factory method cho shadow user
     */
    public static EmailCheckDetailedResponse shadowUser() {
        return EmailCheckDetailedResponse.builder()
                .exists(true)
                .isActive(false)
                .hasPassword(false)
                .userType(UserType.SHADOW_USER)
                .suggestion("Email này đã được dùng từ booking trước. Bạn có thể tiếp tục đặt phòng.")
                .build();
    }
}
