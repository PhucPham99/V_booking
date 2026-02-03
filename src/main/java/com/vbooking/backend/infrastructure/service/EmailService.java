package com.vbooking.backend.infrastructure.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String fullName) {
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        String subject = "Reset Your Password - VBooking";
        String htmlBody = buildPasswordResetEmailHtml(fullName, resetLink);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    /**
     * Send email verification
     */
    public void sendEmailVerification(String toEmail, String verificationToken, String fullName) {
        String verificationLink = "http://localhost:3000/verify-email?token=" + verificationToken;

        String subject = "Verify Your Email - VBooking";
        String htmlBody = buildEmailVerificationHtml(fullName, verificationLink);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    /**
     * Send welcome email
     */
    public void sendWelcomeEmail(String toEmail, String fullName) {
        String subject = "Welcome to VBooking!";
        String htmlBody = buildWelcomeEmailHtml(fullName);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    /**
     * Send booking confirmation with login credentials (for auto-activated shadow
     * users)
     */
    public void sendBookingConfirmationWithCredentials(String toEmail, String fullName, String bookingCode,
            String defaultPassword) {
        String subject = "Xác nhận đặt phòng thành công - Thông tin đăng nhập";
        String htmlBody = buildBookingConfirmationWithCredentialsHtml(fullName, bookingCode, toEmail, defaultPassword);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    /**
     * Send booking confirmation (for existing users)
     */
    public void sendBookingConfirmation(String toEmail, String fullName, String bookingCode) {
        String subject = "Xác nhận đặt phòng thành công";
        String htmlBody = buildBookingConfirmationHtml(fullName, bookingCode);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    /**
     * Generic method to send HTML email
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", toEmail, e);
            throw new RuntimeException("Unexpected email error", e);
        }
    }

    // ======================== HTML EMAIL TEMPLATES ========================

    private String buildPasswordResetEmailHtml(String fullName, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #0066cc; color: white; text-decoration: none; border-radius: 5px; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>VBooking Hotel System</h1>
                        </div>
                        <div class="content">
                            <h2>Password Reset Request</h2>
                            <p>Hi %s,</p>
                            <p>You requested a password reset. Click the button below to reset your password:</p>
                            <p style="text-align: center; margin: 30px 0;">
                                <a href="%s" class="button">Reset Password</a>
                            </p>
                            <p><strong>This link will expire in 1 hour.</strong></p>
                            <p>If you didn't request this, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 VBooking. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(fullName, resetLink);
    }

    private String buildEmailVerificationHtml(String fullName, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #0066cc; color: white; text-decoration: none; border-radius: 5px; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>VBooking Hotel System</h1>
                        </div>
                        <div class="content">
                            <h2>Email Verification</h2>
                            <p>Hi %s,</p>
                            <p>Please verify your email address by clicking the button below:</p>
                            <p style="text-align: center; margin: 30px 0;">
                                <a href="%s" class="button">Verify Email</a>
                            </p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 VBooking. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(fullName, verificationLink);
    }

    private String buildWelcomeEmailHtml(String fullName) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to VBooking!</h1>
                        </div>
                        <div class="content">
                            <h2>Hi %s,</h2>
                            <p>Thank you for joining VBooking! We're excited to have you on board.</p>
                            <p>Start exploring our amazing hotel deals today!</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 VBooking. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(fullName);
    }

    private String buildBookingConfirmationWithCredentialsHtml(String fullName, String bookingCode, String email,
            String password) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #0066cc; color: white; text-decoration: none; border-radius: 5px; }
                        .info-box { background-color: #d1ecf1; border-left: 4px solid: #0066cc; padding: 15px; margin: 20px 0; }
                        .credentials-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
                        .booking-code { font-size: 24px; font-weight: bold; color: #0066cc; text-align: center; margin: 20px 0; }
                        .credential { font-family: monospace; background-color: #f8f9fa; padding: 5px 10px; border-radius: 3px; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Đặt phòng thành công!</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã đặt phòng tại VBooking!</p>

                            <div class="booking-code">
                                Mã đặt phòng: %s
                            </div>

                            <div class="credentials-box">
                                <h3>🔑 Thông tin đăng nhập</h3>
                                <p>Chúng tôi đã tự động tạo tài khoản cho bạn:</p>
                                <ul style="list-style: none; padding: 0;">
                                    <li><strong>Email:</strong> <span class="credential">%s</span></li>
                                    <li><strong>Mật khẩu:</strong> <span class="credential">%s</span></li>
                                </ul>
                                <p style="font-size: 12px; color: #856404; margin-top: 10px;">
                                    ⚠️ Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu để bảo mật tài khoản.
                                </p>
                            </div>

                            <div class="info-box">
                                <h3>✨ Lợi ích khi đăng nhập</h3>
                                <ul>
                                    <li>✓ Quản lý đặt phòng của bạn</li>
                                    <li>✓ Theo dõi lịch sử booking</li>
                                    <li>✓ Nhận voucher <strong>100,000 VND</strong> cho lần đặt đầu tiên!</li>
                                </ul>
                            </div>

                            <p style="text-align: center; margin: 30px 0;">
                                <a href="http://localhost:3000/login" class="button">Đăng nhập ngay</a>
                            </p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 VBooking. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(fullName, bookingCode, email, password);
    }

    private String buildBookingConfirmationHtml(String fullName, String bookingCode) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .booking-code { font-size: 24px; font-weight: bold; color: #0066cc; text-align: center; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Đặt phòng thành công!</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã quay lại sử dụng dịch vụ của VBooking!</p>

                            <div class="booking-code">
                                Mã đặt phòng: %s
                            </div>

                            <p>Vui lòng lưu lại mã này để check-in tại khách sạn.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 VBooking. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(fullName, bookingCode);
    }
}
