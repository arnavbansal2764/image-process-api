package com.example.image_process_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.image_process_api.dto.RegisterRequest;
import com.example.image_process_api.dto.LoginRequest;
import com.example.image_process_api.dto.AuthResponse;
import com.example.image_process_api.dto.FileUploadResponse;
import com.example.image_process_api.dto.PaginatedResponse;
import com.example.image_process_api.dto.TransformationRequest;
import com.example.image_process_api.service.AuthService;
import com.example.image_process_api.service.ImageService;
import com.example.image_process_api.service.ImageTransformationService;
import com.example.image_process_api.exception.AuthException;
import com.example.image_process_api.entity.Image;

import java.io.IOException;


@Controller
@RestController
@RequestMapping("/")
public class main {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private ImageTransformationService imageTransformationService;
    
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
        
        String fileUrl = imageService.uploadImage(
                file.getBytes(),
                file.getOriginalFilename(),
                file.getContentType(),
                null
        );
        
        return new FileUploadResponse(
                fileUrl,
                file.getOriginalFilename(),
                "File uploaded successfully"
        );
    }
    
    @GetMapping("images/{id}")
    public Image getImage(@PathVariable String id) {
        return imageService.getImageById(id);
    }
    
    @GetMapping("images")
    public PaginatedResponse<Image> getImages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return imageService.getImages(page, limit);
    }
    
    @PostMapping("images/{id}/transform")
    public Image transformImage(@PathVariable String id, @RequestBody TransformationRequest transformationRequest) {
        return imageTransformationService.applyTransformations(id, transformationRequest);
    }
    
}

