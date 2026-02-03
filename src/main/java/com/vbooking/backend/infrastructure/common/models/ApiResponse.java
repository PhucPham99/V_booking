package com.vbooking.backend.infrastructure.common.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi trả về JSON
public class ApiResponse<T> {

    private int status;         // HTTP Status Code (200, 400, 404...)
    private String message;     // Thông báo ("Thành công", "Lỗi rồi")
    private T data;             // Dữ liệu trả về (Object, List...)
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Helper method: Trả về thành công
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    // Helper method: Trả về lỗi
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .build();
    }
}