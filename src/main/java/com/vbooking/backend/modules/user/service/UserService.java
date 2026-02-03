package com.vbooking.backend.modules.user.service;

import com.vbooking.backend.infrastructure.exception.AppException;
import com.vbooking.backend.infrastructure.exception.ResourceNotFoundException;
import com.vbooking.backend.infrastructure.service.EmailService;
import com.vbooking.backend.modules.auth.entity.PasswordResetEntity;
import com.vbooking.backend.modules.auth.repository.PasswordResetRepository;
import com.vbooking.backend.modules.user.entity.UserEntity;
import com.vbooking.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * UserService - Quản lý user operations bao gồm shadow user creation
 * cho hybrid booking flow (guest + member)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Tạo shadow user cho guest booking
     * Shadow user = user với passwordHash=null, isActive=false
     * 
     * @param email    Email của guest (bắt buộc)
     * @param phone    Số điện thoại
     * @param fullName Tên đầy đủ
     * @return UserEntity đã được tạo
     * @throws AppException nếu email/phone đã tồn tại
     */
    @Transactional
    public UserEntity createShadowUser(String email, String phone, String fullName) {
        log.info("Creating shadow user for email: {}", email);

        // Validate email không trùng
        if (userRepository.existsByEmail(email)) {
            throw new AppException(HttpStatus.CONFLICT,
                    "Email đã được sử dụng. Vui lòng đăng nhập hoặc sử dụng email khác.");
        }

        // Validate phone không trùng
        if (userRepository.existsByPhone(phone)) {
            throw new AppException(HttpStatus.CONFLICT,
                    "Số điện thoại đã được sử dụng.");
        }

        // Tạo shadow user với password mặc định "1" và auto-activate
        UserEntity shadowUser = UserEntity.builder()
                .email(email)
                .phone(phone)
                .fullName(fullName)
                .passwordHash(passwordEncoder.encode("123456")) // Default password .role("guest")
                .isActive(true) // Auto-activate - không cần verify email
                .provider("local")
                .failedLoginAttempts(0)
                .build();

        UserEntity savedUser = userRepository.save(shadowUser);
        log.info("Shadow user created and auto-activated with ID: {}", savedUser.getUserId());

        return savedUser;
    }

    /**
     * Lấy userId từ email, hoặc tạo shadow user mới nếu chưa tồn tại
     * Đây là method CORE cho hybrid booking flow
     * 
     * @param email             Email của user/guest
     * @param phone             Số điện thoại (dùng nếu tạo mới)
     * @param fullName          Tên đầy đủ (dùng nếu tạo mới)
     * @param createIfNotExists True = tạo shadow user nếu chưa có, False = throw
     *                          exception
     * @return userId để dùng cho booking
     */
    @Transactional
    public Long getUserOrCreateShadow(String email, String phone, String fullName, boolean createIfNotExists) {
        log.debug("Getting or creating user for email: {}", email);

        // Tìm user hiện có theo email
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            log.info("Found existing user with ID: {} for email: {}", user.getUserId(), email);
            return user.getUserId();
        }

        // Nếu không tìm thấy và được phép tạo mới
        if (createIfNotExists) {
            UserEntity shadowUser = createShadowUser(email, phone, fullName);
            return shadowUser.getUserId();
        }

        // Không tìm thấy và không được phép tạo
        throw new AppException(HttpStatus.NOT_FOUND,
                "Không tìm thấy user với email: " + email);
    }

    /**
     * Kích hoạt tài khoản shadow user
     * Chuyển từ shadow user (isActive=false, passwordHash=null)
     * thành full member (isActive=true, có password)
     * 
     * @param email           Email của shadow user
     * @param activationToken Token để verify (lấy từ password_resets table)
     * @param password        Mật khẩu mới để set
     * @throws AppException nếu token không hợp lệ hoặc đã hết hạn
     */
    @Transactional
    public void activateAccount(String email, String activationToken, String password) {
        log.info("Activating account for email: {}", email);

        // 1. Verify activation token (reuse password_resets table)
        PasswordResetEntity resetEntity = passwordResetRepository.findByToken(activationToken)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        "Mã kích hoạt không hợp lệ"));

        // 2. Kiểm tra token chưa hết hạn
        if (resetEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Mã kích hoạt đã hết hạn. Vui lòng yêu cầu gửi lại email kích hoạt.");
        }

        // 3. Lấy user từ DB
        UserEntity user = userRepository.findById(resetEntity.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", resetEntity.getUserId()));

        // 4. Verify email khớp (security check)
        if (!user.getEmail().equals(email)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Email không khớp với mã kích hoạt");
        }

        // 5. Kiểm tra account chưa được kích hoạt
        if (user.getIsActive()) {
            log.warn("Account already activated for email: {}", email);
            // Không throw error - có thể user click link activation nhiều lần
            // Chỉ cần set password mới
        }

        // 6. Kích hoạt account: set password và active=true
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setIsActive(true);
        userRepository.save(user);

        // 7. Xóa activation token đã sử dụng
        passwordResetRepository.delete(resetEntity);

        log.info("Account activated successfully for email: {}", email);
    }

    /**
     * Gửi email kích hoạt tài khoản cho shadow user
     * Tạo activation token và lưu vào password_resets table
     * 
     * @param userId      ID của shadow user
     * @param bookingCode Mã booking (để hiển thị trong email)
     */
    @Transactional
    public void sendAccountActivationEmail(Long userId, String bookingCode) {
        log.info("========================================");
        log.info("DEBUG: sendAccountActivationEmail called");
        log.info("User ID: {}", userId);
        log.info("Booking Code: {}", bookingCode);
        log.info("========================================");

        // 1. Lấy user từ DB
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        log.info("User found: email={}, isActive={}, hasPassword={}",
                user.getEmail(), user.getIsActive(), user.getPasswordHash() != null);

        // 2. ⭐ NEW LOGIC: Shadow user đã auto-activated với password "1"
        // Chỉ cần gửi email với thông tin đăng nhập, không cần activation token
        if (user.getIsActive() && user.getPasswordHash() != null) {
            log.info("Calling emailService.sendBookingConfirmationWithCredentials...");
            // User đã active (bao gồm shadow user mới tạo) - gửi credentials
            emailService.sendBookingConfirmationWithCredentials(
                    user.getEmail(),
                    user.getFullName(),
                    bookingCode,
                    "123456");
            log.info("✅ Booking confirmation with credentials sent to: {}", user.getEmail());
            return;
        }

        // 3. Fallback: Nếu vẫn còn shadow user cũ (isActive=false)
        // (Không nên xảy ra với code mới, nhưng giữ để backward compatibility)
        log.warn("⚠️ User {} is not active. This should not happen with new code.", userId);
        sendBookingConfirmation(userId, bookingCode);
    }

    /**
     * Helper method: Gửi email xác nhận đặt phòng với thông tin đăng nhập
     * (Internal - được gọi từ sendAccountActivationEmail)
     */
    private void sendActivationEmailWithBookingInfo(
            String toEmail,
            String fullName,
            String activationToken,
            String bookingCode) {

        // Call email service with default password "123456"
        emailService.sendBookingConfirmationWithCredentials(toEmail, fullName, bookingCode, "123456");

        log.info("Booking confirmation with login credentials sent to: {}", toEmail);
    }

    /**
     * Gửi email xác nhận đặt phòng (Không có link kích hoạt)
     * Dành cho user đã có tài khoản (isActive=true)
     */
    public void sendBookingConfirmation(Long userId, String bookingCode) {
        UserEntity user = getUserById(userId);

        // Call real email service
        emailService.sendBookingConfirmation(user.getEmail(), user.getFullName(), bookingCode);

        log.info("Booking confirmation email sent to existing user: {}", user.getEmail());
    }

    /**
     * Kiểm tra xem email đã được đăng ký chưa
     * (Expose existing method từ repository cho controller)
     */
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kiểm tra xem phone đã được đăng ký chưa
     */
    public boolean isPhoneRegistered(String phone) {
        return userRepository.existsByPhone(phone);
    }

    /**
     * Lấy user by ID
     */
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Lấy user by email
     */
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
