package com.vbooking.backend.modules.auth.controller;

import com.vbooking.backend.dto.auth.ForgotPasswordRequest;
import com.vbooking.backend.dto.auth.LoginRequest;
import com.vbooking.backend.dto.auth.RefreshTokenRequest;
import com.vbooking.backend.dto.auth.RegisterRequest;
import com.vbooking.backend.dto.auth.ResetPasswordRequest;
import com.vbooking.backend.dto.auth.VerifyResetTokenResponse;
import com.vbooking.backend.dto.auth.ActivateAccountRequest;
import com.vbooking.backend.dto.common.ApiResponse;
import com.vbooking.backend.infrastructure.security.UserPrincipal;
import com.vbooking.backend.dto.auth.ActiveSessionDTO;
import com.vbooking.backend.dto.auth.AuthResponse;
import com.vbooking.backend.dto.auth.ChangePasswordRequest;
import com.vbooking.backend.modules.auth.service.AuthService;
import com.vbooking.backend.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 1. ĐĂNG KÝ TÀI KHOẢN MỚI
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("Đăng ký tài khoản thành công. Vui lòng đăng nhập.");
    }

    /**
     * 2. ĐĂNG NHẬP
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ApiResponse.success(authResponse, "Đăng nhập thành công");
    }

    /**
     * 3. LÀM MỚI ACCESS TOKEN (REFRESH TOKEN)
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success(authResponse, "Token đã được làm mới");
    }

    /**
     * 4. ĐĂNG XUẤT
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success("Đăng xuất thành công");
    }

    /**
     * 5. KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ApiResponse.error("Người dùng chưa đăng nhập");
        }

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .userId(currentUser.getId())
                .email(currentUser.getEmail())
                .fullName(currentUser.getFullName())
                .role(currentUser.getRole())
                .build();

        return ApiResponse.success(userInfo);
    }

    /**
     * 6. KIỂM TRA EMAIL ĐÃ TỒN TẠI
     * GET /api/v1/auth/check-email?email=example@mail.com
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ApiResponse.success(exists, exists ? "Email đã được sử dụng" : "Email khả dụng");
    }

    /**
     * 6B. KIỂM TRA EMAIL CHI TIẾT (Booking.com style)
     * GET /api/v1/auth/check-email-detailed?email=example@mail.com
     * Returns detailed info to show smart login suggestions
     */
    @GetMapping("/check-email-detailed")
    public ApiResponse<com.vbooking.backend.dto.auth.EmailCheckDetailedResponse> checkEmailDetailed(
            @RequestParam String email) {

        Optional<com.vbooking.backend.modules.user.entity.UserEntity> userOpt = userService.getUserByEmail(email);

        if (!userOpt.isPresent()) {
            // Case 1: Email mới - chưa tồn tại
            return ApiResponse.success(
                    com.vbooking.backend.dto.auth.EmailCheckDetailedResponse.newEmail());
        }

        com.vbooking.backend.modules.user.entity.UserEntity user = userOpt.get();

        // Case 2: Active member - có tài khoản đầy đủ
        if (user.getIsActive() && user.getPasswordHash() != null) {
            return ApiResponse.success(
                    com.vbooking.backend.dto.auth.EmailCheckDetailedResponse.activeMember());
        }

        // Case 3: Shadow user - đã booking trước nhưng chưa activate
        return ApiResponse.success(
                com.vbooking.backend.dto.auth.EmailCheckDetailedResponse.shadowUser());
    }

    /**
     * 7. KIỂM TRA SỐ ĐIỆN THOẠI ĐÃ TỒN TẠI
     * GET /api/v1/auth/check-phone?phone=0123456789
     */
    @GetMapping("/check-phone")
    public ApiResponse<Boolean> checkPhoneExists(@RequestParam String phone) {
        boolean exists = authService.phoneExists(phone);
        return ApiResponse.success(exists, exists ? "Số điện thoại đã được sử dụng" : "Số điện thoại khả dụng");
    }

    /**
     * 8. ĐỔI MẬT KHẨU (CHO USER ĐÃ ĐĂNG NHẬP)
     * PUT /api/v1/auth/change-password
     */
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {

        if (currentUser == null) {
            return ApiResponse.error("Vui lòng đăng nhập để đổi mật khẩu");
        }

        authService.changePassword(
                currentUser.getId(),
                request.getCurrentPassword(),
                request.getNewPassword());

        return ApiResponse.success("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
    }

    /**
     * 9. XEM CÁC PHIÊN ĐĂNG NHẬP ĐANG HOẠT ĐỘNG
     * GET /api/v1/auth/sessions
     */
    @GetMapping("/sessions")
    public ApiResponse<List<ActiveSessionDTO>> getActiveSessions(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ApiResponse.error("Vui lòng đăng nhập");
        }

        List<ActiveSessionDTO> sessions = authService.getActiveSessions(currentUser.getId());
        return ApiResponse.success(sessions, "Lấy danh sách phiên đăng nhập thành công");
    }

    /**
     * 10. HỦY MỘT PHIÊN ĐĂNG NHẬP CỤ THỂ
     * DELETE /api/v1/auth/sessions/{tokenId}
     */
    @DeleteMapping("/sessions/{tokenId}")
    public ApiResponse<String> revokeSession(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String tokenId) {

        if (currentUser == null) {
            return ApiResponse.error("Vui lòng đăng nhập");
        }

        authService.revokeSession(currentUser.getId(), tokenId);
        return ApiResponse.success("Hủy phiên đăng nhập thành công");
    }

    /**
     * 11. HỦY TẤT CẢ PHIÊN ĐĂNG NHẬP (LOGOUT ALL DEVICES)
     * DELETE /api/v1/auth/sessions
     */
    @DeleteMapping("/sessions")
    public ApiResponse<String> revokeAllSessions(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ApiResponse.error("Vui lòng đăng nhập");
        }

        authService.revokeAllSessions(currentUser.getId());
        return ApiResponse.success("Đã hủy tất cả phiên đăng nhập. Vui lòng đăng nhập lại.");
    }

    /**
     * 12. QUÊN MẬT KHẨU - Gửi email reset password
     * POST /api/v1/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ApiResponse.success("Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.");
    }

    /**
     * 13. ĐẶT LẠI MẬT KHẨU - Reset password bằng token
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.success("Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập bằng mật khẩu mới.");
    }

    /**
     * 14. KIỂM TRA MÃ RESET PASSWORD - Verify reset token
     * GET /api/v1/auth/verify-reset-token?token=xxx
     */
    @GetMapping("/verify-reset-token")
    public ApiResponse<VerifyResetTokenResponse> verifyResetToken(@RequestParam String token) {
        VerifyResetTokenResponse response = authService.verifyResetToken(token);
        return ApiResponse.success(response);
    }

    /**
     * 15. KÍCH HOẠT TÀI KHOẢN - Activate shadow user account
     * POST /api/v1/auth/activate
     * Used when shadow user clicks activation link from booking confirmation email
     */
    @PostMapping("/activate")
    public ApiResponse<AuthResponse> activateAccount(
            @Valid @RequestBody ActivateAccountRequest request) {
        // 1. Activate account (set password and isActive=true)
        userService.activateAccount(request.getEmail(), request.getToken(), request.getPassword());

        // 2. Auto-login after activation for better UX
        LoginRequest loginRequest = LoginRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        AuthResponse authResponse = authService.login(loginRequest);

        return ApiResponse.success(authResponse,
                "Kích hoạt tài khoản thành công! Chào mừng bạn đến với VBOOKING.");
    }

    // === NESTED DTO CHO /me ENDPOINT ===
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class UserInfoResponse {
        private Long userId;
        private String email;
        private String fullName;
        private String role;
    }
}
