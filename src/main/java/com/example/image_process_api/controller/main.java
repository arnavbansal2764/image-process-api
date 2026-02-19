package com.example.image_process_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.image_process_api.dto.RegisterRequest;
import com.example.image_process_api.dto.LoginRequest;
import com.example.image_process_api.dto.AuthResponse;
import com.example.image_process_api.dto.FileUploadResponse;
import com.example.image_process_api.service.AuthService;
import com.example.image_process_api.service.S3Service;
import com.example.image_process_api.exception.AuthException;

import java.io.IOException;


@Controller
@RestController
@RequestMapping("/")
public class main {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private S3Service s3Service;
    
    @GetMapping()
    public String getHomeString() {
        return new String("Welcome to Image Processing API");
    }

    @PostMapping("register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
    
    @PostMapping("login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
    
    @PostMapping("upload")
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new AuthException("File cannot be empty");
        }
        
        String fileUrl = s3Service.uploadFile(
                file.getBytes(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        
        return new FileUploadResponse(
                fileUrl,
                file.getOriginalFilename(),
                "File uploaded successfully"
        );
    }
    
}
