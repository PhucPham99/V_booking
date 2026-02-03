package com.vbooking.backend.infrastructure.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    
    private final HttpStatus status;
    private final String message;

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    // Constructor mặc định là lỗi 400 (Bad Request)
    public AppException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
    }
}