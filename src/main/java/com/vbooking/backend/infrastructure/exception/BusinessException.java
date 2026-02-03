package com.vbooking.backend.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

	public HttpStatusCode getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}