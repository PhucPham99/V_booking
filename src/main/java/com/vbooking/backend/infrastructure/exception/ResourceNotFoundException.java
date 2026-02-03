package com.vbooking.backend.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor 1: Nhận 3 tham số (QUAN TRỌNG: tham số cuối phải là Object)
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    // Constructor 2: Chỉ nhận 1 message (Dùng cho các trường hợp đơn giản)
    public ResourceNotFoundException(String message) {
        super(message);
    }
}