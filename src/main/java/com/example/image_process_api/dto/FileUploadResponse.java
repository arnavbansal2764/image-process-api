package com.example.image_process_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResponse {
    private String fileUrl;
    private String fileName;
    private String message;
}
