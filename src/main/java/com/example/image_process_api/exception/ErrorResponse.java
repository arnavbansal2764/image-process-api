package com.example.image_process_api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private long timestamp;
}
