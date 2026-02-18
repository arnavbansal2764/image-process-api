package com.example.image_process_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage() != null ? ex.getMessage() : "An error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
