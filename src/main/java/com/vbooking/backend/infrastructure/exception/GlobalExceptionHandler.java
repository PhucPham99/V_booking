package com.vbooking.backend.infrastructure.exception;

import com.vbooking.backend.infrastructure.common.models.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý lỗi ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 2. Xử lý lỗi Business Logic / AppException (400)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // 3. Xử lý lỗi Validate (@Valid failed) -> Trả về danh sách field bị lỗi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Chúng ta format message thành dạng JSON string hoặc nối chuỗi tùy ý
        // Ở đây mình trả về Map lỗi trong phần data
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .data(errors) // Trả về chi tiết field nào sai
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 4. Xử lý lỗi Security (Không có quyền truy cập - 403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền truy cập tài nguyên này.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 5. Xử lý tất cả các lỗi còn lại (500 - Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        // Log lỗi ra console để dev fix (quan trọng)
        ex.printStackTrace(); 
        
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}