package com.vbooking.backend.modules.auth.service;

import com.vbooking.backend.infrastructure.exception.AppException;
import com.vbooking.backend.infrastructure.exception.ResourceNotFoundException;
import com.vbooking.backend.infrastructure.security.JwtTokenProvider;
import com.vbooking.backend.infrastructure.security.UserPrincipal;
import com.vbooking.backend.dto.auth.ActiveSessionDTO;
import com.vbooking.backend.dto.auth.AuthResponse;
import com.vbooking.backend.dto.auth.LoginRequest;
import com.vbooking.backend.dto.auth.RegisterRequest;
import com.vbooking.backend.dto.auth.VerifyResetTokenResponse;
import com.vbooking.backend.modules.auth.entity.PasswordResetEntity;
import com.vbooking.backend.modules.auth.entity.RefreshTokenEntity;
import com.vbooking.backend.modules.auth.repository.PasswordResetRepository;
import com.vbooking.backend.modules.auth.repository.RefreshTokenRepository;
import com.vbooking.backend.infrastructure.service.EmailService;
import com.vbooking.backend.modules.user.entity.UserEntity;
import com.vbooking.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    // --- 1. ĐĂNG KÝ ---
    @Transactional
    public void register(RegisterRequest req) {
        // Check trùng email
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email đã được sử dụng");
        }
        // Check trùng sđt
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Số điện thoại đã được sử dụng");
        }

        // Tạo User mới
        UserEntity user = UserEntity.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role("guest") // Mặc định là guest
                .provider("local")
                .isActive(true)
                .dateOfBirth(req.getDateOfBirth())
                .build();

        userRepository.save(user);
    }

    // --- 2. ĐĂNG NHẬP (Stateful JWT) với Account Lockout ---
    @Transactional
    public AuthResponse login(LoginRequest req) {
        // A. Tìm user theo email để kiểm tra lockout
        UserEntity user = userRepository.findByEmail(req.getEmail())
                .orElse(null);

        // B. Kiểm tra account lockout
        if (user != null && user.getAccountLockedUntil() != null) {
            if (user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
                throw new AppException(HttpStatus.FORBIDDEN,
                        "Tài khoản đã bị khóa do đăng nhập sai nhiều lần. Vui lòng thử lại sau.");
            } else {
                // Hết thời gian khóa → Reset
                user.setAccountLockedUntil(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
        }

        // C. Xác thực bằng AuthenticationManager
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        } catch (Exception e) {
            // Đăng nhập thất bại → Tăng số lần thử sai
            if (user != null) {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

                // Nếu sai >= 5 lần → Khóa tài khoản 15 phút
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
                    userRepository.save(user);
                    throw new AppException(HttpStatus.FORBIDDEN,
                            "Tài khoản đã bị khóa 15 phút do đăng nhập sai 5 lần liên tiếp.");
                }

                userRepository.save(user);
            }
            throw new AppException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Lấy User Entity từ DB
        user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // D. Đăng nhập thành công → Reset failed attempts
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
        }

        // E. Tạo UUID định danh phiên (Token ID)
        String tokenId = UUID.randomUUID().toString();

        // F. Lưu Refresh Token vào DB
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .userId(user.getUserId())
                .token(tokenId)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // G. Tạo Access Token
        String accessToken = jwtTokenProvider.generateAccessToken(user, tokenId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(tokenId)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .role(user.getRole())
                .fullName(user.getFullName())
                .build();
    }

    // --- 3. REFRESH TOKEN (Xoay vòng token - Rotation) ---
    @Transactional
    public AuthResponse refreshToken(String requestRefreshToken) {
        // A. Tìm token trong DB (Dùng hàm findByToken của Repo)
        var storedToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Refresh Token không tồn tại"));

        // B. Kiểm tra tính hợp lệ
        if (storedToken.getIsRevoked()) {
            // Nếu token đã bị hủy mà vẫn mang đi refresh -> Có dấu hiệu tấn công
            // (Ở mức nâng cao: Có thể revoke toàn bộ token của user này để bảo mật)
            throw new AppException(HttpStatus.UNAUTHORIZED,
                    "Refresh token đã bị hủy (Logout). Vui lòng đăng nhập lại.");
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập hết hạn.");
        }

        // C. Lấy User
        UserEntity user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", storedToken.getUserId()));

        // D. Token Rotation: Hủy cái cũ -> Tạo cái mới

        // D1. Hủy token cũ bằng Custom Query (@Modifying) để tối ưu
        refreshTokenRepository.revokeByToken(requestRefreshToken);

        // D2. Tạo Token ID mới
        String newTokenId = UUID.randomUUID().toString();

        // D3. Lưu token mới vào DB
        RefreshTokenEntity newRefreshToken = RefreshTokenEntity.builder()
                .userId(user.getUserId())
                .token(newTokenId)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // D4. Tạo Access Token mới (Nhúng ID mới)
        String newAccessToken = jwtTokenProvider.generateAccessToken(user, newTokenId);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newTokenId)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .role(user.getRole())
                .build();
    }

    // --- 4. LOGOUT ---
    @Transactional
    public void logout(String refreshToken) {
        // Gọi Custom Query (@Modifying) để update isRevoked =true
        // Cách này nhanh hơn: Không cần find -> set -> save
        refreshTokenRepository.revokeByToken(refreshToken);
    }

    // --- 5. KIỂM TRA EMAIL ĐÃ TỒN TẠI ---
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // --- 6. KIỂM TRA SỐ ĐIỆN THOẠI ĐÃ TỒN TẠI ---
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // --- 7. ĐỔI MẬT KHẨU (CHO USER ĐÃ ĐĂNG NHẬP) ---
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        // 1. Lấy user từ DB
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 2. Verify mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng");
        }

        // 3. Validate mật khẩu mới khác mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải khác mật khẩu hiện tại");
        }

        // 4. Hash và update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 5. Optional: Revoke tất cả refresh tokens (force logout all devices)
        // refreshTokenRepository.revokeAllByUserId(userId);
    }

    // --- 8. SESSION MANAGEMENT: XEM CÁC PHIÊN ĐĂNG NHẬP ĐANG HOẠT ĐỘNG ---
    public List<ActiveSessionDTO> getActiveSessions(Long userId) {
        List<RefreshTokenEntity> activeSessions = refreshTokenRepository.findActiveSessionsByUserId(userId);

        return activeSessions.stream()
                .map(session -> ActiveSessionDTO.builder()
                        .tokenId(session.getToken())
                        .createdAt(session.getCreatedAt())
                        .expiryDate(session.getExpiryDate())
                        .isActive(!session.getIsRevoked())
                        .build())
                .collect(Collectors.toList());
    }

    // --- 9. SESSION MANAGEMENT: HỦY MỘT PHIÊN CỤ THỂ ---
    @Transactional
    public void revokeSession(Long userId, String tokenId) {
        // Kiểm tra token có thuộc về user này không
        RefreshTokenEntity token = refreshTokenRepository.findByToken(tokenId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Session không tồn tại"));

        if (!token.getUserId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy session này");
        }

        refreshTokenRepository.revokeByToken(tokenId);
    }

    // --- 10. SESSION MANAGEMENT: HỦY TẤT CẢ PHIÊN (LOGOUT ALL DEVICES) ---
    @Transactional
    public void revokeAllSessions(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    // --- 11. PASSWORD RESET: QUÊN MẬT KHẨU ---
    @Transactional
    public void forgotPassword(String email) {
        // 1. Find user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Email không tồn tại trong hệ thống"));

        // 2. Delete old reset tokens for this user
        passwordResetRepository.deleteByUserId(user.getUserId());

        // 3. Generate secure token
        String resetToken = java.util.UUID.randomUUID().toString();

        // 4. Save reset token (expires in 1 hour)
        PasswordResetEntity resetEntity = PasswordResetEntity.builder()
                .userId(user.getUserId())
                .token(resetToken)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        passwordResetRepository.save(resetEntity);

        // 5. Send email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken, user.getFullName());
    }

    // --- 12. PASSWORD RESET: ĐẶT LẠI MẬT KHẨU ---
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 1. Find and validate token
        PasswordResetEntity resetEntity = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Mã xác thực không hợp lệ"));

        if (resetEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Mã xác thực đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.");
        }

        // 2. Get user
        UserEntity user = userRepository.findById(resetEntity.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", resetEntity.getUserId()));

        // 3. Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Delete used token
        passwordResetRepository.delete(resetEntity);

        // 5. Revoke all sessions (force re-login for security)
        refreshTokenRepository.revokeAllByUserId(user.getUserId());
    }

    // --- 13. PASSWORD RESET: XÁC THỰC TOKEN ---
    public VerifyResetTokenResponse verifyResetToken(String token) {
        java.util.Optional<PasswordResetEntity> resetEntityOpt = passwordResetRepository.findByToken(token);

        if (resetEntityOpt.isEmpty()) {
            return VerifyResetTokenResponse.builder()
                    .valid(false)
                    .message("Mã xác thực không hợp lệ")
                    .build();
        }

        PasswordResetEntity resetEntity = resetEntityOpt.get();

        if (resetEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return VerifyResetTokenResponse.builder()
                    .valid(false)
                    .message("Mã xác thực đã hết hạn")
                    .build();
        }

        UserEntity user = userRepository.findById(resetEntity.getUserId()).orElse(null);

        return VerifyResetTokenResponse.builder()
                .valid(true)
                .email(user != null ? user.getEmail() : null)
                .message("Mã xác thực hợp lệ")
                .build();
    }
}