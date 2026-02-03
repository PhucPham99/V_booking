package com.vbooking.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải có định dạng hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại phải có định dạng Việt Nam (VD: 0901234567 hoặc +84901234567)")
    private String phone;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, max = 50, message = "Họ tên phải từ 3 đến 50 ký tự")
    private String fullName;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
}
